package com.printqueue.service;

import com.printqueue.dto.request.PrintJobRequest;
import com.printqueue.dto.response.PrintJobResponse;
import com.printqueue.entity.PrintJob;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.entity.Role;
import com.printqueue.entity.User;
import com.printqueue.exception.BadRequestException;
import com.printqueue.exception.ResourceNotFoundException;
import com.printqueue.repository.PrintJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintJobService {

    private final PrintJobRepository printJobRepository;
    private final FileStorageService fileStorageService;
    private final JobNumberGenerator jobNumberGenerator;
    private final CostCalculationService costCalculationService;
    private final QueueService queueService;
    private final PrintJobHistoryService historyService;

    @Transactional
    public PrintJobResponse createJob(PrintJobRequest request, MultipartFile file, User user) {
        String filePath = fileStorageService.storeFile(file);
        int numberOfPages = fileStorageService.calculatePageCount(file);
        int totalPages = numberOfPages * request.getNumberOfCopies();
        BigDecimal cost = costCalculationService.calculateCost(numberOfPages, request.getNumberOfCopies(), request.getColorMode());
        String jobNumber = jobNumberGenerator.generateNextJobNumber();

        PrintJob job = PrintJob.builder()
                .jobNumber(jobNumber)
                .user(user)
                .documentName(file.getOriginalFilename())
                .documentPath(filePath)
                .documentType(file.getContentType())
                .fileSize(file.getSize())
                .numberOfPages(numberOfPages)
                .numberOfCopies(request.getNumberOfCopies())
                .totalPages(totalPages)
                .paperSize(request.getPaperSize())
                .colorMode(request.getColorMode())
                .orientation(request.getOrientation())
                .duplex(request.getDuplex())
                .priority(request.getPriority())
                .estimatedCost(cost)
                .status(PrintJobStatus.SUBMITTED)
                .progressPercentage(0)
                .currentPage(0)
                .build();

        PrintJob savedJob = printJobRepository.save(job);
        historyService.logStatusChange(savedJob, null, PrintJobStatus.QUEUED, "Job submitted and queued");
        queueService.refreshQueuePositions();
        return mapToResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public List<PrintJobResponse> getAllJobs() {
        return printJobRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrintJobResponse> getUserJobs(User user) {
        return printJobRepository.findByUserOrderBySubmittedAtDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrintJobResponse getJobById(Long id, User currentUser) {
        PrintJob job = getJobEntityByIdAndCheckAccess(id, currentUser);
        return mapToResponse(job);
    }

    @Transactional
    public void cancelJob(Long id, User currentUser) {
        PrintJob job = getJobEntityByIdAndCheckAccess(id, currentUser);

        if (job.getStatus() == PrintJobStatus.COMPLETED || job.getStatus() == PrintJobStatus.FAILED || job.getStatus() == PrintJobStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel a job that is already " + job.getStatus());
        }

        PrintJobStatus oldStatus = job.getStatus();
        job.setStatus(PrintJobStatus.CANCELLED);
        job.setCancelledAt(LocalDateTime.now());
        printJobRepository.save(job);
        historyService.logStatusChange(job, oldStatus, PrintJobStatus.CANCELLED, "Job cancelled by user");
        queueService.refreshQueuePositions();
    }

    private PrintJob getJobEntityByIdAndCheckAccess(Long id, User currentUser) {
        PrintJob job = printJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrintJob not found with id: " + id));

        if (currentUser.getRole() != Role.ADMIN && !job.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You do not have permission to access this print job");
        }
        return job;
    }

    private PrintJobResponse mapToResponse(PrintJob job) {
        return PrintJobResponse.builder()
                .id(job.getId())
                .jobNumber(job.getJobNumber())
                .userId(job.getUser().getId())
                .documentName(job.getDocumentName())
                .documentType(job.getDocumentType())
                .fileSize(job.getFileSize())
                .numberOfPages(job.getNumberOfPages())
                .numberOfCopies(job.getNumberOfCopies())
                .totalPages(job.getTotalPages())
                .paperSize(job.getPaperSize())
                .colorMode(job.getColorMode())
                .orientation(job.getOrientation())
                .duplex(job.getDuplex())
                .priority(job.getPriority())
                .status(job.getStatus())
                .estimatedCost(job.getEstimatedCost())
                .queuePosition(job.getQueuePosition())
                .progressPercentage(job.getProgressPercentage())
                .currentPage(job.getCurrentPage())
                .submittedAt(job.getSubmittedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .cancelledAt(job.getCancelledAt())
                .errorMessage(job.getErrorMessage())
                .build();
    }
}
