package com.printqueue.dto.response;

import com.printqueue.entity.VirtualPrinterStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrinterResponse {
    private String name;
    private String model;
    private VirtualPrinterStatus status;
    private String activeJobNumber;
    private Integer activeJobProgress;
    private Integer activeJobCurrentPage;
    private Integer activeJobTotalPages;
    private Integer totalJobsProcessed;
}
