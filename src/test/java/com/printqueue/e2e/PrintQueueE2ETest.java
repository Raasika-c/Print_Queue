package com.printqueue.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printqueue.dto.request.LoginRequest;
import com.printqueue.dto.request.PriorityUpdateRequest;
import com.printqueue.dto.request.RegisterRequest;
import com.printqueue.entity.JobPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@org.junit.jupiter.api.TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
@DisplayName("End-to-End System Integration Tests")
class PrintQueueE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String userToken;
    private static Long testJobId;
    private static String adminToken;

    @Test
    @Order(1)
    @DisplayName("E2E-01: User Registration Flow")
    void testUserRegistration() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("E2E Student");
        req.setEmail("student.e2e@example.com");
        req.setMobile("9876543210");
        req.setPassword("Password@123");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("student.e2e@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        userToken = json.get("token").asText();
        assertThat(userToken).isNotEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("E2E-02: User Login Flow")
    void testUserLogin() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("student.e2e@example.com");
        req.setPassword("Password@123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        userToken = json.get("token").asText();
    }

    @Test
    @Order(3)
    @DisplayName("E2E-03: Submit Print Job Flow")
    void testSubmitJob() throws Exception {
        MockMultipartFile docFile = new MockMultipartFile(
                "file",
                "assignment.pdf",
                "application/pdf",
                "Sample PDF content for testing".getBytes()
        );

        String jobDetailsJson = "{"
                + "\"numberOfPages\": 5,"
                + "\"numberOfCopies\": 2,"
                + "\"paperSize\": \"A4\","
                + "\"colorMode\": \"COLOR\","
                + "\"orientation\": \"PORTRAIT\","
                + "\"duplex\": false,"
                + "\"priority\": \"NORMAL\""
                + "}";

        MockMultipartFile jobDetails = new MockMultipartFile(
                "jobDetails",
                "",
                "application/json",
                jobDetailsJson.getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/jobs")
                        .file(docFile)
                        .file(jobDetails)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        testJobId = json.get("id").asLong();
        assertThat(testJobId).isNotNull();

        assertThat(json.get("jobNumber").asText()).isNotEmpty();
        assertThat(json.get("documentName").asText()).isEqualTo("assignment.pdf");
        assertThat(json.get("totalPages").asInt()).isEqualTo(2); // 1 page * 2 copies
        assertThat(json.get("estimatedCost").asDouble()).isEqualTo(10.0); // 2 total pages * ₹5
    }

    @Test
    @Order(4)
    @DisplayName("E2E-04: View Queue Flow")
    void testViewQueue() throws Exception {
        mockMvc.perform(get("/api/queue")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].position").value(1));

        mockMvc.perform(get("/api/queue/status")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeJobs").value(1))
                .andExpect(jsonPath("$.totalPagesInQueue").value(2));
    }

    @Test
    @Order(5)
    @DisplayName("E2E-05: Track Job & History Flow")
    void testTrackJobAndHistory() throws Exception {
        mockMvc.perform(get("/api/jobs/" + testJobId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testJobId))
                .andExpect(jsonPath("$.documentName").value("assignment.pdf"));

        mockMvc.perform(get("/api/jobs/" + testJobId + "/history")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].newStatus").value("QUEUED"));
    }

    @Test
    @Order(6)
    @DisplayName("E2E-06: Cancel Job Flow")
    void testCancelJob() throws Exception {
        mockMvc.perform(delete("/api/jobs/" + testJobId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        // Verify status changed to CANCELLED
        mockMvc.perform(get("/api/jobs/" + testJobId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.queuePosition").isEmpty());

        // Verify queue is now empty
        mockMvc.perform(get("/api/queue")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("E2E-07: Admin Login Flow")
    void testAdminLogin() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@digitalprint.com");
        req.setPassword("Admin@123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = json.get("token").asText();
    }

    @Test
    @Order(8)
    @DisplayName("E2E-08: Admin Queue Management & Priority Update")
    void testAdminQueueAndPriority() throws Exception {
        // Create a new job to manage
        MockMultipartFile docFile = new MockMultipartFile(
                "file", "admin_test.pdf", "application/pdf", "Content".getBytes()
        );
        MockMultipartFile jobDetails = new MockMultipartFile(
                "jobDetails", "", "application/json",
                "{\"numberOfPages\":2,\"numberOfCopies\":1,\"priority\":\"LOW\",\"colorMode\":\"BLACK_WHITE\",\"paperSize\":\"A4\",\"orientation\":\"PORTRAIT\",\"duplex\":false}".getBytes()
        );

        MvcResult createResult = mockMvc.perform(multipart("/api/jobs")
                        .file(docFile).file(jobDetails)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long adminJobId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Admin gets all jobs
        mockMvc.perform(get("/api/admin/jobs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Admin changes priority to HIGH
        PriorityUpdateRequest priorityReq = new PriorityUpdateRequest();
        priorityReq.setPriority(JobPriority.HIGH);

        mockMvc.perform(put("/api/admin/jobs/" + adminJobId + "/priority")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityReq)))
                .andExpect(status().isOk());

        // Admin checks stats
        mockMvc.perform(get("/api/admin/statistics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobs").isNotEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("E2E-09: Printer Hardware Control Flow")
    void testPrinterControl() throws Exception {
        // Get status
        mockMvc.perform(get("/api/printer/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PRINTER-01"));

        // Admin triggers error
        mockMvc.perform(post("/api/printer/error")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify ERROR status
        mockMvc.perform(get("/api/printer/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));

        // Admin resets printer
        mockMvc.perform(post("/api/printer/reset")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify IDLE status
        mockMvc.perform(get("/api/printer/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IDLE"));
    }
}
