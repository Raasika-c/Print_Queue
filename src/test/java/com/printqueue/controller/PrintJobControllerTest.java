package com.printqueue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printqueue.dto.request.PrintJobRequest;
import com.printqueue.entity.*;
import com.printqueue.security.JwtAuthenticationEntryPoint;
import com.printqueue.security.JwtTokenProvider;
import com.printqueue.service.PrintJobService;
import com.printqueue.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrintJobController.class)
@Import({com.printqueue.config.SecurityConfig.class,
         com.printqueue.security.JwtTokenProvider.class,
         com.printqueue.security.JwtAuthenticationFilter.class,
         com.printqueue.security.JwtAuthenticationEntryPoint.class,
         com.printqueue.security.UserDetailsServiceImpl.class})
@org.springframework.test.context.ActiveProfiles("test")
class PrintJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrintJobService printJobService;

    @MockBean
    private UserService userService;

    @MockBean
    private com.printqueue.service.PrintJobHistoryService historyService;

    @MockBean
    private com.printqueue.repository.UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void testCreateJobValidationFailure() throws Exception {
        PrintJobRequest request = new PrintJobRequest();
        // Missing required fields
        
        MockMultipartFile jobDetailsPart = new MockMultipartFile(
                "jobDetails", 
                "", 
                "application/json", 
                objectMapper.writeValueAsBytes(request));
                
        MockMultipartFile filePart = new MockMultipartFile(
                "file", 
                "test.pdf", 
                "application/pdf", 
                "dummy content".getBytes());

        mockMvc.perform(multipart("/api/jobs")
                .file(jobDetailsPart)
                .file(filePart)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateJobUnauthorized() throws Exception {
        int status = mockMvc.perform(multipart("/api/jobs")
                .with(csrf()))
                .andReturn().getResponse().getStatus();
        org.assertj.core.api.Assertions.assertThat(status).isNotEqualTo(200);
    }
}
