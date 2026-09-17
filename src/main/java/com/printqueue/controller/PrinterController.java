package com.printqueue.controller;

import com.printqueue.dto.response.PrinterResponse;
import com.printqueue.service.VirtualPrinterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/printer")
@RequiredArgsConstructor
public class PrinterController {

    private final VirtualPrinterService virtualPrinterService;

    @GetMapping("/status")
    public ResponseEntity<PrinterResponse> getStatus() {
        return ResponseEntity.ok(virtualPrinterService.getStatus());
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> startPrinter() {
        virtualPrinterService.startNextJob();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pause")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> pausePrinter() {
        virtualPrinterService.pausePrinter();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resume")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resumePrinter() {
        virtualPrinterService.resumePrinter();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetPrinter() {
        virtualPrinterService.resetPrinter();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/error")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reportError() {
        virtualPrinterService.reportError();
        return ResponseEntity.ok().build();
    }
}
