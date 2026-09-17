package com.printqueue.consistency;

import com.printqueue.entity.*;
import com.printqueue.exception.BadRequestException;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.PrinterRepository;
import com.printqueue.service.PrintingSimulationService;
import com.printqueue.service.QueueService;
import com.printqueue.service.VirtualPrinterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Printer State Consistency & FSM Transition Tests")
class PrinterStateConsistencyTest {

    @Mock
    private PrinterRepository printerRepository;

    @Mock
    private PrintJobRepository printJobRepository;

    @Mock
    private QueueService queueService;

    @Mock
    private PrintingSimulationService printingSimulationService;

    @Mock
    private com.printqueue.service.PrintJobHistoryService historyService;

    @InjectMocks
    private VirtualPrinterService virtualPrinterService;

    private VirtualPrinter printer;

    @BeforeEach
    void setUp() {
        printer = VirtualPrinter.builder()
                .id(1L)
                .name("PRINTER-01")
                .status(VirtualPrinterStatus.IDLE)
                .totalJobsProcessed(0)
                .build();

        when(printerRepository.findByName("PRINTER-01")).thenReturn(Optional.of(printer));
    }

    @Test
    @DisplayName("FSM-01: Pause rejected when printer is IDLE")
    void testPauseWhenIdleThrows() {
        printer.setStatus(VirtualPrinterStatus.IDLE);
        assertThatThrownBy(() -> virtualPrinterService.pausePrinter())
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only PRINTING printers can be paused");
    }

    @Test
    @DisplayName("FSM-02: Resume rejected when printer is IDLE or PRINTING")
    void testResumeWhenNotPausedThrows() {
        printer.setStatus(VirtualPrinterStatus.IDLE);
        assertThatThrownBy(() -> virtualPrinterService.resumePrinter())
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only PAUSED printers can be resumed");
    }

    @Test
    @DisplayName("FSM-03: Reset rejected when printer is IDLE or PRINTING")
    void testResetWhenNotErrorThrows() {
        printer.setStatus(VirtualPrinterStatus.PRINTING);
        assertThatThrownBy(() -> virtualPrinterService.resetPrinter())
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only ERROR or OFFLINE printers can be reset");
    }

    @Test
    @DisplayName("FSM-04: Reset transitions ERROR -> IDLE successfully")
    void testResetFromErrorToIdle() {
        printer.setStatus(VirtualPrinterStatus.ERROR);
        virtualPrinterService.resetPrinter();
        assertThat(printer.getStatus()).isEqualTo(VirtualPrinterStatus.IDLE);
    }
}
