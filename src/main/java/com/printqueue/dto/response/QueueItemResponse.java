package com.printqueue.dto.response;

import com.printqueue.entity.JobPriority;
import com.printqueue.entity.PrintJobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QueueItemResponse {
    private Integer position;
    private String jobNumber;
    private String documentName;
    private String userName;
    private Integer pages;
    private Integer copies;
    private JobPriority priority;
    private PrintJobStatus status;
    private LocalDateTime submittedAt;
}
