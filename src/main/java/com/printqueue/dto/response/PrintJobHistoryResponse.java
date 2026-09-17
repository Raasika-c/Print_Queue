package com.printqueue.dto.response;

import com.printqueue.entity.PrintJobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PrintJobHistoryResponse {
    private Long id;
    private Long printJobId;
    private PrintJobStatus oldStatus;
    private PrintJobStatus newStatus;
    private String message;
    private String changedBy;
    private LocalDateTime changedAt;
}
