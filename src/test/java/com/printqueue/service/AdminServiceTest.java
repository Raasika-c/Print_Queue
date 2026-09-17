package com.printqueue.service;

import com.printqueue.dto.response.AdminStatisticsResponse;
import com.printqueue.dto.response.QueueStatusResponse;
import com.printqueue.entity.*;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PrintJobRepository printJobRepository;

    @Mock
    private PrintJobService printJobService;

    @Mock
    private QueueService queueService;

    @Mock
    private PrintJobHistoryService historyService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    private PrintJob testJob;
    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        testJob = new PrintJob();
        testJob.setId(10L);
        testJob.setStatus(PrintJobStatus.QUEUED);
        testJob.setPriority(JobPriority.NORMAL);
        testJob.setNumberOfPages(5);
        testJob.setNumberOfCopies(2);
        testJob.setTotalPages(10);
    }

    @Test
    void testGetStatistics() {
        when(userRepository.count()).thenReturn(5L);
        when(printJobRepository.count()).thenReturn(3L);
        
        PrintJob completedJob = new PrintJob();
        completedJob.setStatus(PrintJobStatus.COMPLETED);
        completedJob.setTotalPages(15);
        completedJob.setNumberOfCopies(3);
        
        when(printJobRepository.findAll()).thenReturn(Arrays.asList(testJob, completedJob));
        
        QueueStatusResponse qsr = QueueStatusResponse.builder().activeJobs(1).build();
        when(queueService.getQueueStatus()).thenReturn(qsr);
        
        AdminStatisticsResponse stats = adminService.getStatistics();
        
        assertThat(stats.getTotalUsers()).isEqualTo(5L);
        assertThat(stats.getTotalJobs()).isEqualTo(3L);
        assertThat(stats.getQueuedJobs()).isEqualTo(1L);
        assertThat(stats.getCompletedJobs()).isEqualTo(1L);
        assertThat(stats.getCurrentQueueLength()).isEqualTo(1);
        assertThat(stats.getTotalPagesPrinted()).isEqualTo(15L);
        assertThat(stats.getTotalCopiesPrinted()).isEqualTo(3L);
    }

    @Test
    void testChangePriority() {
        when(printJobRepository.findById(10L)).thenReturn(Optional.of(testJob));
        
        adminService.changeJobPriority(10L, JobPriority.HIGH);
        
        assertThat(testJob.getPriority()).isEqualTo(JobPriority.HIGH);
        verify(printJobRepository).save(testJob);
        verify(queueService).refreshQueuePositions();
    }

    @Test
    void testRetryFailedJob() {
        testJob.setStatus(PrintJobStatus.FAILED);
        testJob.setErrorMessage("Error");
        
        when(printJobRepository.findById(10L)).thenReturn(Optional.of(testJob));
        
        adminService.retryFailedJob(10L);
        
        assertThat(testJob.getStatus()).isEqualTo(PrintJobStatus.QUEUED);
        assertThat(testJob.getErrorMessage()).isNull();
        
        verify(printJobRepository).save(testJob);
        verify(historyService).logStatusChange(eq(testJob), eq(PrintJobStatus.FAILED), eq(PrintJobStatus.QUEUED), anyString());
        verify(queueService).refreshQueuePositions();
    }

    @Test
    void testCancelJob() {
        when(userService.getCurrentUserEntity()).thenReturn(adminUser);
        adminService.cancelJob(10L);
        verify(printJobService).cancelJob(10L, adminUser);
    }
}
