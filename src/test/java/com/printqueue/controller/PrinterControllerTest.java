package com.printqueue.controller;

import com.printqueue.dto.response.PrinterResponse;
import com.printqueue.entity.VirtualPrinterStatus;
import com.printqueue.security.JwtAuthenticationEntryPoint;
import com.printqueue.service.VirtualPrinterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrinterController.class)
@Import({com.printqueue.config.SecurityConfig.class,
         com.printqueue.security.JwtTokenProvider.class,
         com.printqueue.security.JwtAuthenticationFilter.class,
         com.printqueue.security.JwtAuthenticationEntryPoint.class,
         com.printqueue.security.UserDetailsServiceImpl.class})
@org.springframework.test.context.ActiveProfiles("test")
class PrinterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VirtualPrinterService virtualPrinterService;

    @MockBean
    private com.printqueue.repository.UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testGetStatus() throws Exception {
        PrinterResponse response = PrinterResponse.builder()
                .name("PRINTER-01")
                .status(VirtualPrinterStatus.IDLE)
                .build();
                
        when(virtualPrinterService.getStatus()).thenReturn(response);
        
        mockMvc.perform(get("/api/printer/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PRINTER-01"))
                .andExpect(jsonPath("$.status").value("IDLE"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testStartPrinter() throws Exception {
        mockMvc.perform(post("/api/printer/start").with(csrf()))
                .andExpect(status().isOk());
    }
}
