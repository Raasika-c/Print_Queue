package com.printqueue.service;

import com.printqueue.dto.response.PrinterResponse;
import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.entity.VirtualPrinter;
import com.printqueue.entity.VirtualPrinterStatus;
import com.printqueue.exception.BadRequestException;
import com.printqueue.exception.ResourceNotFoundException;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.PrinterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualPrinterService {

    private final PrinterRepository printerRepository;
    private final PrintJobRepository printJobRepository;
    private final QueueService queueService;
    private final PrintingSimulationService printingSimulationService;
    private final PrintJobHistoryService historyService;

    private static final String PRINTER_NAME = "PRINTER-01";

    @Transactional(readOnly = true)
    public PrinterResponse getStatus() {
        VirtualPrinter printer = getPrinter();
        Optional<PrintJob> activeJob = printJobRepository.findByPrinterAndStatus(printer, PrintJobStatus.PRINTING);

        PrinterResponse.PrinterResponseBuilder builder = PrinterResponse.builder()
                .name(printer.getName())
                .model(printer.getModel())
                .status(printer.getStatus())
                .totalJobsProcessed(printer.getTotalJobsProcessed());

        activeJob.ifPresent(job -> builder
                .activeJobNumber(job.getJobNumber())
                .activeJobProgress(job.getProgressPercentage())
                .activeJobCurrentPage(job.getCurrentPage())
                .activeJobTotalPages(job.getTotalPages()));

        return builder.build();
    }

    @Transactional
    public void startNextJob() {
        VirtualPrinter printer = getPrinter();
        if (printer.getStatus() != VirtualPrinterStatus.IDLE) {
            throw new BadRequestException("Printer is not IDLE");
        }

        var queue = queueService.getQueue();
        if (queue.isEmpty()) {
            return; // Nothing to print
        }

        // Get top job (position 1)
        String nextJobNumber = queue.get(0).getJobNumber();
        PrintJob job = printJobRepository.findAll().stream()
                .filter(j -> j.getJobNumber().equals(nextJobNumber))
                .findFirst()
                .orElseThrow();

        printer.setStatus(VirtualPrinterStatus.PRINTING);
        printer.setLastActiveAt(LocalDateTime.now());
        printerRepository.save(printer);

        PrintJobStatus oldStatus = job.getStatus();
        job.setStatus(PrintJobStatus.PRINTING);
        job.setPrinter(printer);
        job.setStartedAt(LocalDateTime.now());
        printJobRepository.save(job);
        
        historyService.logStatusChange(job, oldStatus, PrintJobStatus.PRINTING, "Job started printing");
        
        queueService.refreshQueuePositions();

        printingSimulationService.simulatePrintJob(job.getId(), printer.getId());
    }

    @Transactional
    public void pausePrinter() {
        VirtualPrinter printer = getPrinter();
        if (printer.getStatus() != VirtualPrinterStatus.PRINTING) {
            throw new BadRequestException("Only PRINTING printers can be paused");
        }
        
        printer.setStatus(VirtualPrinterStatus.PAUSED);
        printerRepository.save(printer);

        printJobRepository.findByPrinterAndStatus(printer, PrintJobStatus.PRINTING)
                .ifPresent(job -> {
                    PrintJobStatus old = job.getStatus();
                    job.setStatus(PrintJobStatus.PAUSED);
                    printJobRepository.save(job);
                    historyService.logStatusChange(job, old, PrintJobStatus.PAUSED, "Printer paused by admin");
                });
    }

    @Transactional
    public void resumePrinter() {
        VirtualPrinter printer = getPrinter();
        if (printer.getStatus() != VirtualPrinterStatus.PAUSED) {
            throw new BadRequestException("Only PAUSED printers can be resumed");
        }

        printer.setStatus(VirtualPrinterStatus.PRINTING);
        printerRepository.save(printer);

        printJobRepository.findByPrinterAndStatus(printer, PrintJobStatus.PAUSED)
                .ifPresent(job -> {
                    PrintJobStatus old = job.getStatus();
                    job.setStatus(PrintJobStatus.PRINTING);
                    printJobRepository.save(job);
                    historyService.logStatusChange(job, old, PrintJobStatus.PRINTING, "Printer resumed by admin");
                    printingSimulationService.simulatePrintJob(job.getId(), printer.getId());
                });
    }

    @Transactional
    public void reportError() {
        VirtualPrinter printer = getPrinter();
        printer.setStatus(VirtualPrinterStatus.ERROR);
        printerRepository.save(printer);

        // Mark active job as FAILED
        printJobRepository.findByPrinterAndStatus(printer, PrintJobStatus.PRINTING)
                .ifPresent(job -> {
                    PrintJobStatus old = job.getStatus();
                    job.setStatus(PrintJobStatus.FAILED);
                    job.setErrorMessage("Printer encountered an error");
                    job.setCompletedAt(LocalDateTime.now());
                    printJobRepository.save(job);
                    historyService.logStatusChange(job, old, PrintJobStatus.FAILED, "Job failed due to printer error");
                    queueService.refreshQueuePositions();
                });
    }

    @Transactional
    public void resetPrinter() {
        VirtualPrinter printer = getPrinter();
        if (printer.getStatus() != VirtualPrinterStatus.ERROR && printer.getStatus() != VirtualPrinterStatus.OFFLINE) {
            throw new BadRequestException("Only ERROR or OFFLINE printers can be reset");
        }
        
        printer.setStatus(VirtualPrinterStatus.IDLE);
        printerRepository.save(printer);
    }

    private VirtualPrinter getPrinter() {
        return printerRepository.findByName(PRINTER_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Printer not found: " + PRINTER_NAME));
    }
}
