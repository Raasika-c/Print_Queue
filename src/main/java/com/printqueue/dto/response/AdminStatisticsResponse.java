package com.printqueue.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatisticsResponse {
    private long totalUsers;
    private long totalJobs;
    private long queuedJobs;
    private long printingJobs;
    private long completedJobs;
    private long cancelledJobs;
    private long failedJobs;
    private int currentQueueLength;
    private long totalPagesPrinted;
    private long totalCopiesPrinted;
}
