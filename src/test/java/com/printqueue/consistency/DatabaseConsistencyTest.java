package com.printqueue.consistency;

import com.printqueue.entity.*;
import com.printqueue.repository.PrintJobHistoryRepository;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Database Consistency & Relational Integrity Tests")
class DatabaseConsistencyTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrintJobRepository printJobRepository;

    @Autowired
    private PrintJobHistoryRepository historyRepository;

    @Test
    @DisplayName("DB-01: Unique Email Constraint Enforcement")
    void testUniqueEmailConstraint() {
        User user1 = User.builder()
                .name("Alice Unique")
                .email("duplicate@printqueue.com")
                .password("Hash@12345")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.saveAndFlush(user1);

        User user2 = User.builder()
                .name("Bob Duplicate")
                .email("duplicate@printqueue.com")
                .password("Hash@67890")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("DB-02: Foreign Key Relationship between User and PrintJob")
    void testUserPrintJobForeignKey() {
        User user = User.builder()
                .name("Charlie Owner")
                .email("charlie@printqueue.com")
                .password("Hash@12345")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.saveAndFlush(user);

        PrintJob job = PrintJob.builder()
                .jobNumber("DPQ-2026-9001")
                .user(savedUser)
                .documentName("thesis.pdf")
                .documentPath("/uploads/thesis.pdf")
                .documentType("application/pdf")
                .fileSize(1024L)
                .numberOfPages(10)
                .numberOfCopies(1)
                .totalPages(10)
                .paperSize(PaperSize.A4)
                .colorMode(ColorMode.BLACK_WHITE)
                .orientation(Orientation.PORTRAIT)
                .duplex(false)
                .priority(JobPriority.NORMAL)
                .status(PrintJobStatus.QUEUED)
                .estimatedCost(BigDecimal.valueOf(10.00))
                .build();

        PrintJob savedJob = printJobRepository.saveAndFlush(job);
        assertThat(savedJob.getId()).isNotNull();
        assertThat(savedJob.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("DB-03: PrintJobHistory Audit Cascade & FK Integrity")
    void testJobHistoryForeignKey() {
        User user = User.builder()
                .name("Diana Audit")
                .email("diana@printqueue.com")
                .password("Hash@12345")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.saveAndFlush(user);

        PrintJob job = PrintJob.builder()
                .jobNumber("DPQ-2026-9002")
                .user(savedUser)
                .documentName("audit.pdf")
                .documentPath("/uploads/audit.pdf")
                .documentType("application/pdf")
                .fileSize(2048L)
                .numberOfPages(2)
                .numberOfCopies(1)
                .totalPages(2)
                .paperSize(PaperSize.A4)
                .colorMode(ColorMode.COLOR)
                .orientation(Orientation.PORTRAIT)
                .duplex(true)
                .priority(JobPriority.HIGH)
                .status(PrintJobStatus.QUEUED)
                .estimatedCost(BigDecimal.valueOf(10.00))
                .build();
        PrintJob savedJob = printJobRepository.saveAndFlush(job);

        PrintJobHistory history = PrintJobHistory.builder()
                .printJob(savedJob)
                .oldStatus(null)
                .newStatus(PrintJobStatus.QUEUED)
                .message("Job submitted")
                .changedBy("diana@printqueue.com")
                .changedAt(LocalDateTime.now())
                .build();
        PrintJobHistory savedHistory = historyRepository.saveAndFlush(history);

        assertThat(savedHistory.getId()).isNotNull();
        assertThat(savedHistory.getPrintJob().getId()).isEqualTo(savedJob.getId());
    }
}
