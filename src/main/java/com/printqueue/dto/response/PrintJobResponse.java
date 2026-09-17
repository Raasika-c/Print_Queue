package com.printqueue.dto.response;

import com.printqueue.entity.ColorMode;
import com.printqueue.entity.JobPriority;
import com.printqueue.entity.Orientation;
import com.printqueue.entity.PaperSize;
import com.printqueue.entity.PrintJobStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PrintJobResponse {
    private Long id;
    private String jobNumber;
    private Long userId;
    private String documentName;
    private String documentType;
    private Long fileSize;
    private Integer numberOfPages;
    private Integer numberOfCopies;
    private Integer totalPages;
    private PaperSize paperSize;
    private ColorMode colorMode;
    private Orientation orientation;
    private Boolean duplex;
    private JobPriority priority;
    private PrintJobStatus status;
    private BigDecimal estimatedCost;
    private Integer queuePosition;
    private Integer progressPercentage;
    private Integer currentPage;
    private LocalDateTime submittedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String errorMessage;
}
