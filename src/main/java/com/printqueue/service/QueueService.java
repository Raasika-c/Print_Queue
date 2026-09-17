package com.printqueue.service;

import com.printqueue.dto.response.QueueItemResponse;
import com.printqueue.dto.response.QueueStatusResponse;
import com.printqueue.entity.JobPriority;
import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.repository.PrintJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

    private final PrintJobRepository printJobRepository;

    @Transactional
    public void refreshQueuePositions() {
        List<PrintJob> activeJobs = getSortedActiveJobs();
        
        // Reset positions for completed/cancelled jobs
        List<PrintJob> inactiveJobs = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.COMPLETED || j.getStatus() == PrintJobStatus.CANCELLED || j.getStatus() == PrintJobStatus.FAILED)
                .filter(j -> j.getQueuePosition() != null)
                .collect(Collectors.toList());
        
        for (PrintJob inactiveJob : inactiveJobs) {
            inactiveJob.setQueuePosition(null);
            printJobRepository.save(inactiveJob);
        }

        // Update active jobs positions
        for (int i = 0; i < activeJobs.size(); i++) {
            PrintJob job = activeJobs.get(i);
            Integer newPosition = i + 1;
            if (!newPosition.equals(job.getQueuePosition())) {
                job.setQueuePosition(newPosition);
                printJobRepository.save(job);
            }
        }
        log.info("Queue positions refreshed. Active jobs: {}", activeJobs.size());
    }

    @Transactional(readOnly = true)
    public List<QueueItemResponse> getQueue() {
        List<PrintJob> activeJobs = getSortedActiveJobs();
        
        return activeJobs.stream()
                .map(job -> QueueItemResponse.builder()
                        .position(activeJobs.indexOf(job) + 1)
                        .jobNumber(job.getJobNumber())
                        .documentName(job.getDocumentName())
                        .userName(job.getUser().getName())
                        .pages(job.getNumberOfPages())
                        .copies(job.getNumberOfCopies())
                        .priority(job.getPriority())
                        .status(job.getStatus())
                        .submittedAt(job.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QueueStatusResponse getQueueStatus() {
        List<PrintJob> activeJobs = getSortedActiveJobs();
        int totalPages = activeJobs.stream()
                .mapToInt(PrintJob::getTotalPages)
                .sum();
                
        return QueueStatusResponse.builder()
                .activeJobs(activeJobs.size())
                .totalPagesInQueue(totalPages)
                .queueState("ACTIVE") // Can be updated if queue pause feature is added
                .build();
    }
    
    private List<PrintJob> getSortedActiveJobs() {
        List<PrintJob> jobs = printJobRepository.findByStatusIn(
                List.of(PrintJobStatus.SUBMITTED, PrintJobStatus.QUEUED)
        );
        
        jobs.sort(Comparator.comparing((PrintJob p) -> getPriorityWeight(p.getPriority())).reversed()
                            .thenComparing(PrintJob::getSubmittedAt));
        return jobs;
    }

    private int getPriorityWeight(JobPriority priority) {
        if (priority == null) return 1;
        switch (priority) {
            case HIGH: return 3;
            case NORMAL: return 2;
            case LOW: return 1;
            default: return 1;
        }
    }
}
