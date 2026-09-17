package com.printqueue.controller;

import com.printqueue.dto.request.PriorityUpdateRequest;
import com.printqueue.dto.response.AdminStatisticsResponse;
import com.printqueue.dto.response.PrintJobResponse;
import com.printqueue.dto.response.UserResponse;
import com.printqueue.service.AdminService;
import com.printqueue.service.PrintJobService;
import com.printqueue.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final PrintJobService printJobService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<PrintJobResponse>> getAllJobs() {
        return ResponseEntity.ok(printJobService.getAllJobs());
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    @PutMapping("/jobs/{id}/priority")
    public ResponseEntity<Void> changeJobPriority(@PathVariable Long id, @Valid @RequestBody PriorityUpdateRequest request) {
        adminService.changeJobPriority(id, request.getPriority());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/jobs/{id}/cancel")
    public ResponseEntity<Void> cancelJob(@PathVariable Long id) {
        adminService.cancelJob(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/jobs/{id}/retry")
    public ResponseEntity<Void> retryFailedJob(@PathVariable Long id) {
        adminService.retryFailedJob(id);
        return ResponseEntity.ok().build();
    }
}
