package com.printqueue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "print_job_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintJobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "print_job_id", nullable = false)
    private PrintJob printJob;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private PrintJobStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private PrintJobStatus newStatus;

    @Column(length = 255)
    private String message;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}
