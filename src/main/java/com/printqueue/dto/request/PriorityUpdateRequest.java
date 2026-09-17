package com.printqueue.dto.request;

import com.printqueue.entity.JobPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriorityUpdateRequest {
    @NotNull(message = "Priority is required")
    private JobPriority priority;
}
