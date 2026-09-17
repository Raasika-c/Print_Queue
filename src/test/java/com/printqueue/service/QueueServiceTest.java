package com.printqueue.service;

import com.printqueue.dto.response.QueueItemResponse;
import com.printqueue.entity.*;
import com.printqueue.repository.PrintJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private PrintJobRepository printJobRepository;

    @InjectMocks
    private QueueService queueService;

    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
    }

    @Test
    void testQueueOrderingFIFOAndPriority() {
        PrintJob job1 = createJob(1L, JobPriority.NORMAL, LocalDateTime.now().minusMinutes(10));
        PrintJob job2 = createJob(2L, JobPriority.HIGH, LocalDateTime.now().minusMinutes(5));
        PrintJob job3 = createJob(3L, JobPriority.NORMAL, LocalDateTime.now().minusMinutes(15));
        PrintJob job4 = createJob(4L, JobPriority.LOW, LocalDateTime.now().minusMinutes(20));

        when(printJobRepository.findByStatusIn(anyList())).thenReturn(Arrays.asList(job1, job2, job3, job4));

        List<QueueItemResponse> queue = queueService.getQueue();

        // Expected order:
        // 1. job2 (HIGH)
        // 2. job3 (NORMAL, oldest)
        // 3. job1 (NORMAL)
        // 4. job4 (LOW)
        assertThat(queue).hasSize(4);
        assertThat(queue.get(0).getJobNumber()).isEqualTo("JOB-2"); // HIGH
        assertThat(queue.get(1).getJobNumber()).isEqualTo("JOB-3"); // NORMAL, -15m
        assertThat(queue.get(2).getJobNumber()).isEqualTo("JOB-1"); // NORMAL, -10m
        assertThat(queue.get(3).getJobNumber()).isEqualTo("JOB-4"); // LOW
        
        // Check positions
        assertThat(queue.get(0).getPosition()).isEqualTo(1);
        assertThat(queue.get(1).getPosition()).isEqualTo(2);
        assertThat(queue.get(2).getPosition()).isEqualTo(3);
        assertThat(queue.get(3).getPosition()).isEqualTo(4);
    }
    
    @Test
    void testRefreshQueuePositions() {
        PrintJob activeJob1 = createJob(1L, JobPriority.HIGH, LocalDateTime.now().minusMinutes(5));
        PrintJob activeJob2 = createJob(2L, JobPriority.NORMAL, LocalDateTime.now());
        
        PrintJob completedJob = createJob(3L, JobPriority.NORMAL, LocalDateTime.now());
        completedJob.setStatus(PrintJobStatus.COMPLETED);
        completedJob.setQueuePosition(1); // Old position
        
        when(printJobRepository.findByStatusIn(anyList())).thenReturn(Arrays.asList(activeJob1, activeJob2));
        when(printJobRepository.findAll()).thenReturn(Arrays.asList(activeJob1, activeJob2, completedJob));
        
        queueService.refreshQueuePositions();
        
        // active jobs should get their new positions saved
        verify(printJobRepository, atLeastOnce()).save(activeJob1);
        verify(printJobRepository, atLeastOnce()).save(activeJob2);
        assertThat(activeJob1.getQueuePosition()).isEqualTo(1);
        assertThat(activeJob2.getQueuePosition()).isEqualTo(2);
        
        // inactive jobs should have position set to null
        verify(printJobRepository, atLeastOnce()).save(completedJob);
        assertThat(completedJob.getQueuePosition()).isNull();
    }

    @Test
    void testPriorityChangeQueueConsistency() {
        PrintJob job1 = createJob(1L, JobPriority.NORMAL, LocalDateTime.now().minusMinutes(10));
        PrintJob job2 = createJob(2L, JobPriority.NORMAL, LocalDateTime.now());
        
        when(printJobRepository.findByStatusIn(anyList())).thenReturn(Arrays.asList(job1, job2));
        when(printJobRepository.findAll()).thenReturn(Arrays.asList(job1, job2));
        
        queueService.refreshQueuePositions();
        assertThat(job1.getQueuePosition()).isEqualTo(1);
        assertThat(job2.getQueuePosition()).isEqualTo(2);
        
        // Change priority of job2 to HIGH
        job2.setPriority(JobPriority.HIGH);
        
        queueService.refreshQueuePositions();
        assertThat(job2.getQueuePosition()).isEqualTo(1);
        assertThat(job1.getQueuePosition()).isEqualTo(2);
    }

    private PrintJob createJob(Long id, JobPriority priority, LocalDateTime submittedAt) {
        PrintJob job = new PrintJob();
        job.setId(id);
        job.setJobNumber("JOB-" + id);
        job.setUser(testUser);
        job.setPriority(priority);
        job.setSubmittedAt(submittedAt);
        job.setStatus(PrintJobStatus.QUEUED);
        job.setNumberOfPages(1);
        job.setNumberOfCopies(1);
        job.setTotalPages(1);
        return job;
    }
}
