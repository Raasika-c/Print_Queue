package com.printqueue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printqueue.dto.request.PriorityUpdateRequest;
import com.printqueue.dto.response.AdminStatisticsResponse;
import com.printqueue.entity.JobPriority;
import com.printqueue.service.AdminService;
import com.printqueue.service.PrintJobService;
import com.printqueue.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({com.printqueue.config.SecurityConfig.class,
         com.printqueue.security.JwtTokenProvider.class,
         com.printqueue.security.JwtAuthenticationFilter.class,
         com.printqueue.security.JwtAuthenticationEntryPoint.class,
         com.printqueue.security.UserDetailsServiceImpl.class})
@org.springframework.test.context.ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    @MockBean
    private PrintJobService printJobService;

    @MockBean
    private com.printqueue.repository.UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testGetStatistics_AdminAccess() throws Exception {
        AdminStatisticsResponse stats = AdminStatisticsResponse.builder()
                .totalUsers(10)
                .totalJobs(50)
                .build();
                
        when(adminService.getStatistics()).thenReturn(stats);
        
        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.totalJobs").value(50));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void testGetStatistics_UserAccess_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testChangePriority() throws Exception {
        PriorityUpdateRequest request = new PriorityUpdateRequest();
        request.setPriority(JobPriority.HIGH);
        
        mockMvc.perform(put("/api/admin/jobs/1/priority")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testRetryFailedJob() throws Exception {
        mockMvc.perform(put("/api/admin/jobs/1/retry").with(csrf()))
                .andExpect(status().isOk());
    }
}
