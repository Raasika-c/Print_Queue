package com.printqueue.service;

import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobHistory;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.repository.PrintJobHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PrintJobHistoryServiceTest {

    @Mock
    private PrintJobHistoryRepository historyRepository;

    @InjectMocks
    private PrintJobHistoryService historyService;

    @Test
    void testLogStatusChange() {
        PrintJob job = new PrintJob();
        job.setId(10L);
        job.setJobNumber("JOB-10");

        historyService.logStatusChange(job, PrintJobStatus.QUEUED, PrintJobStatus.PRINTING, "Started printing");

        ArgumentCaptor<PrintJobHistory> captor = ArgumentCaptor.forClass(PrintJobHistory.class);
        verify(historyRepository).save(captor.capture());

        PrintJobHistory savedHistory = captor.getValue();
        assertThat(savedHistory.getPrintJob()).isEqualTo(job);
        assertThat(savedHistory.getOldStatus()).isEqualTo(PrintJobStatus.QUEUED);
        assertThat(savedHistory.getNewStatus()).isEqualTo(PrintJobStatus.PRINTING);
        assertThat(savedHistory.getMessage()).isEqualTo("Started printing");
        assertThat(savedHistory.getChangedBy()).isEqualTo("SYSTEM");
    }
}
