package com.printqueue.service;

import com.printqueue.dto.response.QueueItemResponse;
import com.printqueue.entity.*;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.PrinterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualPrinterServiceTest {

    @Mock
    private PrinterRepository printerRepository;

    @Mock
    private PrintJobRepository printJobRepository;

    @Mock
    private QueueService queueService;

    @Mock
    private PrintingSimulationService printingSimulationService;

    @Mock
    private PrintJobHistoryService historyService;

    @InjectMocks
    private VirtualPrinterService virtualPrinterService;

    private VirtualPrinter printer;

    @BeforeEach
    void setUp() {
        printer = new VirtualPrinter();
        printer.setId(1L);
        printer.setName("PRINTER-01");
        printer.setStatus(VirtualPrinterStatus.IDLE);
        printer.setTotalJobsProcessed(0);
        
        when(printerRepository.findByName("PRINTER-01")).thenReturn(Optional.of(printer));
    }

    @Test
    void testStartNextJob() {
        QueueItemResponse topJob = QueueItemResponse.builder()
                .jobNumber("JOB-1")
                .build();
                
        when(queueService.getQueue()).thenReturn(Collections.singletonList(topJob));
        
        PrintJob job = new PrintJob();
        job.setId(10L);
        job.setJobNumber("JOB-1");
        when(printJobRepository.findAll()).thenReturn(Collections.singletonList(job));
        
        virtualPrinterService.startNextJob();
        
        assertThat(printer.getStatus()).isEqualTo(VirtualPrinterStatus.PRINTING);
        assertThat(job.getStatus()).isEqualTo(PrintJobStatus.PRINTING);
        
        verify(printerRepository).save(printer);
        verify(printJobRepository).save(job);
        verify(queueService).refreshQueuePositions();
        verify(printingSimulationService).simulatePrintJob(10L, 1L);
    }
    
    @Test
    void testPauseAndResume() {
        printer.setStatus(VirtualPrinterStatus.PRINTING);
        
        PrintJob activeJob = new PrintJob();
        activeJob.setId(10L);
        activeJob.setStatus(PrintJobStatus.PRINTING);
        
        when(printJobRepository.findByPrinterAndStatus(printer, PrintJobStatus.PRINTING))
                .thenReturn(Optional.of(activeJob));
                
        virtualPrinterService.pausePrinter();
        
        assertThat(printer.getStatus()).isEqualTo(VirtualPrinterStatus.PAUSED);
        assertThat(activeJob.getStatus()).isEqualTo(PrintJobStatus.PAUSED);
        
        when(printJobRepository.findByPrinterAndStatus(printer, PrintJobStatus.PAUSED))
                .thenReturn(Optional.of(activeJob));
                
        virtualPrinterService.resumePrinter();
        
        assertThat(printer.getStatus()).isEqualTo(VirtualPrinterStatus.PRINTING);
        assertThat(activeJob.getStatus()).isEqualTo(PrintJobStatus.PRINTING);
    }
}
