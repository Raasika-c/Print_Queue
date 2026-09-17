package com.printqueue.controller;

import com.printqueue.dto.response.QueueItemResponse;
import com.printqueue.dto.response.QueueStatusResponse;
import com.printqueue.entity.JobPriority;
import com.printqueue.entity.PrintJobStatus;
import com.printqueue.security.JwtAuthenticationEntryPoint;
import com.printqueue.service.QueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
@Import({com.printqueue.config.SecurityConfig.class,
         com.printqueue.security.JwtTokenProvider.class,
         com.printqueue.security.JwtAuthenticationFilter.class,
         com.printqueue.security.JwtAuthenticationEntryPoint.class,
         com.printqueue.security.UserDetailsServiceImpl.class})
@org.springframework.test.context.ActiveProfiles("test")
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueService queueService;

    @MockBean
    private com.printqueue.repository.UserRepository userRepository;

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void testGetQueue() throws Exception {
        QueueItemResponse item = QueueItemResponse.builder()
                .position(1)
                .jobNumber("DPQ-2026-0001")
                .documentName("doc1.pdf")
                .userName("Test User")
                .pages(5)
                .copies(2)
                .priority(JobPriority.HIGH)
                .status(PrintJobStatus.QUEUED)
                .submittedAt(LocalDateTime.now())
                .build();

        when(queueService.getQueue()).thenReturn(Collections.singletonList(item));

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].jobNumber").value("DPQ-2026-0001"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void testGetQueueStatus() throws Exception {
        QueueStatusResponse statusResponse = QueueStatusResponse.builder()
                .activeJobs(1)
                .totalPagesInQueue(10)
                .queueState("ACTIVE")
                .build();

        when(queueService.getQueueStatus()).thenReturn(statusResponse);

        mockMvc.perform(get("/api/queue/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeJobs").value(1))
                .andExpect(jsonPath("$.totalPagesInQueue").value(10))
                .andExpect(jsonPath("$.queueState").value("ACTIVE"));
    }
}
