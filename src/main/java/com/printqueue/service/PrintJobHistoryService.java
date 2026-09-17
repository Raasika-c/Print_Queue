package com.printqueue.service;

import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobHistory;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.repository.PrintJobHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintJobHistoryService {

    private final PrintJobHistoryRepository historyRepository;

    @Transactional
    public void logStatusChange(PrintJob job, PrintJobStatus oldStatus, PrintJobStatus newStatus, String message) {
        String changedBy = "SYSTEM";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            changedBy = auth.getName();
        }

        PrintJobHistory history = PrintJobHistory.builder()
                .printJob(job)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .message(message)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
        log.info("Job {} status changed from {} to {} by {}", job.getJobNumber(), oldStatus, newStatus, changedBy);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.printqueue.dto.response.PrintJobHistoryResponse> getHistoryForJob(Long jobId) {
        return historyRepository.findByPrintJobIdOrderByChangedAtDesc(jobId).stream()
                .map(h -> com.printqueue.dto.response.PrintJobHistoryResponse.builder()
                        .id(h.getId())
                        .printJobId(h.getPrintJob().getId())
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .message(h.getMessage())
                        .changedBy(h.getChangedBy())
                        .changedAt(h.getChangedAt())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }
}
