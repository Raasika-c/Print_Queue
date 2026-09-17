package com.printqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "print_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_number", nullable = false, unique = true, length = 20)
    private String jobNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "printer_id")
    private VirtualPrinter printer;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "file_path")
    private String documentPath;

    @Column(name = "document_type", length = 100)
    private String documentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "number_of_pages", nullable = false)
    private Integer numberOfPages;

    @Column(name = "number_of_copies", nullable = false)
    private Integer numberOfCopies;

    @Column(name = "total_pages", nullable = false)
    private Integer totalPages;

    @Enumerated(EnumType.STRING)
    @Column(name = "paper_size", nullable = false)
    private PaperSize paperSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "color_mode", nullable = false)
    private ColorMode colorMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "orientation", nullable = false)
    private Orientation orientation;

    @Column(nullable = false)
    private Boolean duplex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrintJobStatus status;

    @Column(name = "estimated_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage;

    @Column(name = "current_page", nullable = false)
    private Integer currentPage;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @PrePersist
    protected void onCreate() {
        if (status == null) status = PrintJobStatus.SUBMITTED;
        if (duplex == null) duplex = false;
        if (progressPercentage == null) progressPercentage = 0;
        if (currentPage == null) currentPage = 0;
        if (priority == null) priority = JobPriority.NORMAL;
    }
}
