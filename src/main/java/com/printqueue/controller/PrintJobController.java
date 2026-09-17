package com.printqueue.controller;

import com.printqueue.dto.request.PrintJobRequest;
import com.printqueue.dto.response.PrintJobResponse;
import com.printqueue.entity.Role;
import com.printqueue.entity.User;
import com.printqueue.service.PrintJobService;
import com.printqueue.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class PrintJobController {

    private final PrintJobService printJobService;
    private final UserService userService;
    private final com.printqueue.service.PrintJobHistoryService historyService;

    @PostMapping
    public ResponseEntity<PrintJobResponse> createJob(
            @Valid @RequestPart("jobDetails") PrintJobRequest request,
            @RequestPart("file") MultipartFile file) {
        User currentUser = userService.getCurrentUserEntity();
        PrintJobResponse response = printJobService.createJob(request, file, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PrintJobResponse>> getAllJobs() {
        User currentUser = userService.getCurrentUserEntity();
        List<PrintJobResponse> jobs;
        if (currentUser.getRole() == Role.ADMIN) {
            jobs = printJobService.getAllJobs();
        } else {
            jobs = printJobService.getUserJobs(currentUser);
        }
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrintJobResponse> getJobById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUserEntity();
        PrintJobResponse response = printJobService.getJobById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<com.printqueue.dto.response.PrintJobHistoryResponse>> getJobHistory(@PathVariable Long id) {
        User currentUser = userService.getCurrentUserEntity();
        // ensure user has access to this job
        printJobService.getJobById(id, currentUser);
        return ResponseEntity.ok(historyService.getHistoryForJob(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelJob(@PathVariable Long id) {
        User currentUser = userService.getCurrentUserEntity();
        printJobService.cancelJob(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
