package com.printqueue.service;

import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.entity.VirtualPrinter;
import com.printqueue.entity.VirtualPrinterStatus;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.PrinterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PrintingSimulationService {

    private final PrintJobRepository printJobRepository;
    private final PrinterRepository printerRepository;
    private final QueueService queueService;
    private final VirtualPrinterService virtualPrinterService;
    private final PrintJobHistoryService historyService;

    @Value("${app.printer.processing-seconds-per-page:1}")
    private int secondsPerPage;

    public PrintingSimulationService(PrintJobRepository printJobRepository,
                                     PrinterRepository printerRepository,
                                     QueueService queueService,
                                     PrintJobHistoryService historyService,
                                     @Lazy VirtualPrinterService virtualPrinterService) {
        this.printJobRepository = printJobRepository;
        this.printerRepository = printerRepository;
        this.queueService = queueService;
        this.historyService = historyService;
        this.virtualPrinterService = virtualPrinterService;
    }

    public void simulatePrintJob(Long jobId, Long printerId) {
        CompletableFuture.runAsync(() -> {
            try {
                boolean completed = false;
                while (!completed) {
                    // Sleep for simulated time
                    Thread.sleep(secondsPerPage * 1000L);

                    // Update state in a transaction
                    completed = updatePrintProgress(jobId, printerId);
                    
                    if (completed) {
                        break;
                    }
                }
                
                // Try to start the next job
                virtualPrinterService.startNextJob();
                
            } catch (InterruptedException e) {
                log.error("Print simulation interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Error during print simulation", e);
            }
        });
    }

    @Transactional
    public boolean updatePrintProgress(Long jobId, Long printerId) {
        VirtualPrinter printer = printerRepository.findById(printerId).orElse(null);
        PrintJob job = printJobRepository.findById(jobId).orElse(null);

        if (printer == null || job == null) {
            return true; // Stop simulation
        }

        if (printer.getStatus() != VirtualPrinterStatus.PRINTING) {
            // Printer was paused, errored, or taken offline.
            return true; // Stop this simulation thread
        }

        if (job.getStatus() != PrintJobStatus.PRINTING) {
            return true; // Stop this simulation thread
        }

        int currentPage = job.getCurrentPage() + 1;
        job.setCurrentPage(currentPage);
        
        int progress = (int) (((double) currentPage / job.getTotalPages()) * 100);
        job.setProgressPercentage(progress);

        if (currentPage >= job.getTotalPages()) {
            PrintJobStatus oldStatus = job.getStatus();
            job.setStatus(PrintJobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            job.setProgressPercentage(100);
            
            printer.setStatus(VirtualPrinterStatus.IDLE);
            printer.setTotalJobsProcessed(printer.getTotalJobsProcessed() + 1);
            
            printJobRepository.save(job);
            printerRepository.save(printer);
            
            historyService.logStatusChange(job, oldStatus, PrintJobStatus.COMPLETED, "Print job completed successfully");
            
            queueService.refreshQueuePositions();
            
            return true; // Completed
        } else {
            printJobRepository.save(job);
            return false; // Not completed yet
        }
    }
}
