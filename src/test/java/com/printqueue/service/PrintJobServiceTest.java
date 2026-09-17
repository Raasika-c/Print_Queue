package com.printqueue.service;

import com.printqueue.dto.request.PrintJobRequest;
import com.printqueue.dto.response.PrintJobResponse;
import com.printqueue.entity.*;
import com.printqueue.exception.BadRequestException;
import com.printqueue.repository.PrintJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrintJobServiceTest {

    @Mock
    private PrintJobRepository printJobRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private JobNumberGenerator jobNumberGenerator;

    @Mock
    private CostCalculationService costCalculationService;

    @Mock
    private QueueService queueService;

    @Mock
    private PrintJobHistoryService historyService;

    @InjectMocks
    private PrintJobService printJobService;

    private User testUser;
    private PrintJobRequest request;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(Role.USER);

        request = new PrintJobRequest();
        request.setNumberOfCopies(2);
        request.setColorMode(ColorMode.BLACK_WHITE);
        request.setPaperSize(PaperSize.A4);
        request.setOrientation(Orientation.PORTRAIT);
        request.setDuplex(false);
        request.setPriority(JobPriority.NORMAL);

        file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy content".getBytes());
    }

    @Test
    void testCreateJob() {
        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("uploads/test.pdf");
        when(fileStorageService.calculatePageCount(any(MultipartFile.class))).thenReturn(5);
        when(costCalculationService.calculateCost(5, 2, ColorMode.BLACK_WHITE)).thenReturn(new BigDecimal("10.00"));
        when(jobNumberGenerator.generateNextJobNumber()).thenReturn("DPQ-2026-0001");
        
        PrintJob savedJob = new PrintJob();
        savedJob.setId(100L);
        savedJob.setJobNumber("DPQ-2026-0001");
        savedJob.setUser(testUser);
        savedJob.setDocumentName("test.pdf");
        savedJob.setNumberOfPages(5);
        savedJob.setNumberOfCopies(2);
        savedJob.setTotalPages(10);
        savedJob.setEstimatedCost(new BigDecimal("10.00"));
        savedJob.setStatus(PrintJobStatus.SUBMITTED);
        
        when(printJobRepository.save(any(PrintJob.class))).thenReturn(savedJob);

        PrintJobResponse response = printJobService.createJob(request, file, testUser);

        assertThat(response).isNotNull();
        assertThat(response.getJobNumber()).isEqualTo("DPQ-2026-0001");
        assertThat(response.getStatus()).isEqualTo(PrintJobStatus.SUBMITTED);
        verify(printJobRepository, times(1)).save(any(PrintJob.class));
    }

    @Test
    void testCancelJobSuccess() {
        PrintJob job = new PrintJob();
        job.setId(1L);
        job.setUser(testUser);
        job.setStatus(PrintJobStatus.SUBMITTED);
        
        when(printJobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        printJobService.cancelJob(1L, testUser);
        
        assertThat(job.getStatus()).isEqualTo(PrintJobStatus.CANCELLED);
        verify(printJobRepository, times(1)).save(job);
    }

    @Test
    void testCancelCompletedJobThrowsException() {
        PrintJob job = new PrintJob();
        job.setId(1L);
        job.setUser(testUser);
        job.setStatus(PrintJobStatus.COMPLETED);
        
        when(printJobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        assertThrows(BadRequestException.class, () -> printJobService.cancelJob(1L, testUser));
    }

    @Test
    void testAccessDeniedForOtherUserJob() {
        PrintJob job = new PrintJob();
        job.setId(1L);
        User otherUser = new User();
        otherUser.setId(2L);
        job.setUser(otherUser);
        
        when(printJobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        assertThrows(BadRequestException.class, () -> printJobService.getJobById(1L, testUser));
    }
    
    @Test
    void testAdminCanAccessOtherUserJob() {
        PrintJob job = new PrintJob();
        job.setId(1L);
        User otherUser = new User();
        otherUser.setId(2L);
        job.setUser(otherUser);
        
        User admin = new User();
        admin.setId(3L);
        admin.setRole(Role.ADMIN);
        
        when(printJobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        PrintJobResponse response = printJobService.getJobById(1L, admin);
        assertThat(response).isNotNull();
    }
}
