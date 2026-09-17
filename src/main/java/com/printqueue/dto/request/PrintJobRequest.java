package com.printqueue.dto.request;

import com.printqueue.entity.ColorMode;
import com.printqueue.entity.JobPriority;
import com.printqueue.entity.Orientation;
import com.printqueue.entity.PaperSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrintJobRequest {

    @NotNull(message = "Number of copies is required")
    @Min(value = 1, message = "At least 1 copy is required")
    @Max(value = 50, message = "Maximum 50 copies allowed")
    private Integer numberOfCopies;

    @NotNull(message = "Paper size is required")
    private PaperSize paperSize;

    @NotNull(message = "Color mode is required")
    private ColorMode colorMode;

    @NotNull(message = "Orientation is required")
    private Orientation orientation;

    @NotNull(message = "Duplex option is required")
    private Boolean duplex;

    @NotNull(message = "Priority is required")
    private JobPriority priority;
}
