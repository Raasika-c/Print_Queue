package com.printqueue.controller;

import com.printqueue.dto.response.QueueItemResponse;
import com.printqueue.dto.response.QueueStatusResponse;
import com.printqueue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping
    public ResponseEntity<List<QueueItemResponse>> getQueue() {
        return ResponseEntity.ok(queueService.getQueue());
    }

    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getQueueStatus() {
        return ResponseEntity.ok(queueService.getQueueStatus());
    }
}
