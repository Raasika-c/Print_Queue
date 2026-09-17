package com.printqueue.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Frontend Pages Availability Tests")
class FrontendPagesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {
            "/index.html",
            "/login.html",
            "/register.html",
            "/dashboard.html",
            "/submit-job.html",
            "/my-jobs.html",
            "/job-details.html",
            "/queue.html",
            "/admin-dashboard.html",
            "/admin-queue.html",
            "/users.html",
            "/printer.html",
            "/error.html"
    })
    @DisplayName("Verify static HTML pages load with 200 OK")
    void testPageServesSuccessfully(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verify root URL / serves index.html with 200 OK")
    void testRootUrlServes() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
