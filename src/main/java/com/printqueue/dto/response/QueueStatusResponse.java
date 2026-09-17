package com.printqueue.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueStatusResponse {
    private int activeJobs;
    private int totalPagesInQueue;
    private String queueState; // e.g. ACTIVE, PAUSED
}
