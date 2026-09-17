package com.printqueue.consistency;

import com.printqueue.dto.response.QueueItemResponse;
import com.printqueue.entity.*;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.service.QueueService;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Queue Ordering & Dynamic Re-indexing Consistency Tests")
class QueueConsistencyTest {

    @Mock
    private PrintJobRepository printJobRepository;

    @InjectMocks
    private QueueService queueService;

    @Test
    @DisplayName("QUEUE-01: Multi-User Interleaving with Priority and FIFO")
    void testMultiUserQueueOrdering() {
        User user1 = User.builder().id(1L).name("User One").build();
        User user2 = User.builder().id(2L).name("User Two").build();

        LocalDateTime t0 = LocalDateTime.now().minusMinutes(20);
        LocalDateTime t1 = LocalDateTime.now().minusMinutes(15);
        LocalDateTime t2 = LocalDateTime.now().minusMinutes(10);
        LocalDateTime t3 = LocalDateTime.now().minusMinutes(5);

        // User 1 submitted NORMAL at t0
        PrintJob j1 = PrintJob.builder().id(1L).jobNumber("J1").user(user1).priority(JobPriority.NORMAL).submittedAt(t0).status(PrintJobStatus.QUEUED).numberOfPages(1).numberOfCopies(1).totalPages(1).build();
        // User 2 submitted LOW at t1
        PrintJob j2 = PrintJob.builder().id(2L).jobNumber("J2").user(user2).priority(JobPriority.LOW).submittedAt(t1).status(PrintJobStatus.QUEUED).numberOfPages(1).numberOfCopies(1).totalPages(1).build();
        // User 2 submitted HIGH at t2 (jumps ahead of J1 and J2)
        PrintJob j3 = PrintJob.builder().id(3L).jobNumber("J3").user(user2).priority(JobPriority.HIGH).submittedAt(t2).status(PrintJobStatus.QUEUED).numberOfPages(1).numberOfCopies(1).totalPages(1).build();
        // User 1 submitted NORMAL at t3
        PrintJob j4 = PrintJob.builder().id(4L).jobNumber("J4").user(user1).priority(JobPriority.NORMAL).submittedAt(t3).status(PrintJobStatus.QUEUED).numberOfPages(1).numberOfCopies(1).totalPages(1).build();

        when(printJobRepository.findByStatusIn(anyList())).thenReturn(Arrays.asList(j1, j2, j3, j4));

        List<QueueItemResponse> queue = queueService.getQueue();

        assertThat(queue).hasSize(4);
        assertThat(queue.get(0).getJobNumber()).isEqualTo("J3"); // HIGH (Pos 1)
        assertThat(queue.get(0).getPosition()).isEqualTo(1);

        assertThat(queue.get(1).getJobNumber()).isEqualTo("J1"); // NORMAL @ t0 (Pos 2)
        assertThat(queue.get(1).getPosition()).isEqualTo(2);

        assertThat(queue.get(2).getJobNumber()).isEqualTo("J4"); // NORMAL @ t3 (Pos 3)
        assertThat(queue.get(2).getPosition()).isEqualTo(3);

        assertThat(queue.get(3).getJobNumber()).isEqualTo("J2"); // LOW @ t1 (Pos 4)
        assertThat(queue.get(3).getPosition()).isEqualTo(4);
    }
}
