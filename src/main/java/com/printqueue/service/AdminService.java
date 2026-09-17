package com.printqueue.service;

import com.printqueue.dto.response.AdminStatisticsResponse;
import com.printqueue.entity.JobPriority;
import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.exception.BadRequestException;
import com.printqueue.exception.ResourceNotFoundException;
import com.printqueue.entity.User;
import com.printqueue.repository.PrintJobRepository;
import com.printqueue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PrintJobRepository printJobRepository;
    private final PrintJobService printJobService;
    private final QueueService queueService;
    private final PrintJobHistoryService historyService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public AdminStatisticsResponse getStatistics() {
        long totalUsers = userRepository.count();
        long totalJobs = printJobRepository.count();
        
        long queuedJobs = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.QUEUED).count();
        long printingJobs = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.PRINTING).count();
        long completedJobs = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.COMPLETED).count();
        long cancelledJobs = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.CANCELLED).count();
        long failedJobs = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.FAILED).count();

        int currentQueueLength = queueService.getQueueStatus().getActiveJobs();
        
        long totalPages = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.COMPLETED)
                .mapToLong(PrintJob::getTotalPages)
                .sum();
                
        long totalCopies = printJobRepository.findAll().stream()
                .filter(j -> j.getStatus() == PrintJobStatus.COMPLETED)
                .mapToLong(PrintJob::getNumberOfCopies)
                .sum();

        return AdminStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalJobs(totalJobs)
                .queuedJobs(queuedJobs)
                .printingJobs(printingJobs)
                .completedJobs(completedJobs)
                .cancelledJobs(cancelledJobs)
                .failedJobs(failedJobs)
                .currentQueueLength(currentQueueLength)
                .totalPagesPrinted(totalPages)
                .totalCopiesPrinted(totalCopies)
                .build();
    }

    @Transactional
    public void changeJobPriority(Long id, JobPriority newPriority) {
        PrintJob job = getJob(id);
        
        if (job.getStatus() != PrintJobStatus.QUEUED && job.getStatus() != PrintJobStatus.SUBMITTED) {
            throw new BadRequestException("Can only change priority of queued jobs");
        }

        job.setPriority(newPriority);
        printJobRepository.save(job);
        
        // Log is not exactly a status change, but we can log priority change if we want.
        // historyService.logStatusChange(job, job.getStatus(), job.getStatus(), "Priority changed to " + newPriority);
        
        queueService.refreshQueuePositions();
    }

    @Transactional
    public void retryFailedJob(Long id) {
        PrintJob job = getJob(id);
        
        if (job.getStatus() != PrintJobStatus.FAILED) {
            throw new BadRequestException("Can only retry failed jobs");
        }
        
        PrintJobStatus oldStatus = job.getStatus();
        
        job.setStatus(PrintJobStatus.QUEUED);
        job.setErrorMessage(null);
        job.setPrinter(null);
        job.setProgressPercentage(0);
        job.setCurrentPage(0);
        
        printJobRepository.save(job);
        historyService.logStatusChange(job, oldStatus, PrintJobStatus.QUEUED, "Failed job retried by Admin");
        
        queueService.refreshQueuePositions();
    }
    
    @Transactional
    public void cancelJob(Long id) {
        User adminUser = userService.getCurrentUserEntity();
        printJobService.cancelJob(id, adminUser);
    }

    private PrintJob getJob(Long id) {
        return printJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrintJob not found"));
    }
}
