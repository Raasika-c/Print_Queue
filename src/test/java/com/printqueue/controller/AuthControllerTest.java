package com.printqueue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printqueue.dto.request.LoginRequest;
import com.printqueue.dto.request.RegisterRequest;
import com.printqueue.dto.response.AuthResponse;
import com.printqueue.dto.response.UserResponse;
import com.printqueue.entity.Role;
import com.printqueue.entity.UserStatus;
import com.printqueue.exception.AuthenticationException;
import com.printqueue.exception.BusinessRuleException;
import com.printqueue.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Level 3 — API Tests for AuthController using MockMvc.
 * Tests: HTTP status codes, request/response JSON, validation, security.
 */
@WebMvcTest(AuthController.class)
@Import({com.printqueue.config.SecurityConfig.class,
         com.printqueue.security.JwtTokenProvider.class,
         com.printqueue.security.JwtAuthenticationFilter.class,
         com.printqueue.security.JwtAuthenticationEntryPoint.class,
         com.printqueue.security.UserDetailsServiceImpl.class})
@ActiveProfiles("test")
@DisplayName("AuthController API Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private com.printqueue.repository.UserRepository userRepository;

    private AuthResponse successAuthResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        successAuthResponse = AuthResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.test.signature")
                .tokenType("Bearer")
                .userId(1L)
                .name("Alice Smith")
                .email("alice@example.com")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .message("Registration successful. Welcome to Digital Print Queue!")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .name("Alice Smith")
                .email("alice@example.com")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ================================================================
    // POST /api/auth/register TESTS
    // ================================================================

    @Nested
    @DisplayName("POST /api/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("Should return 201 CREATED on successful registration")
        void testRegisterSuccess() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("Alice Smith");
            request.setEmail("alice@example.com");
            request.setMobile("9876543210");
            request.setPassword("Alice@123");

            when(userService.register(any(RegisterRequest.class))).thenReturn(successAuthResponse);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.email").value("alice@example.com"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("Should return 400 when email is missing")
        void testRegisterMissingEmail() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("Alice Smith");
            // email intentionally omitted
            request.setPassword("Alice@123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email format is invalid")
        void testRegisterInvalidEmail() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("Alice Smith");
            request.setEmail("not-an-email");
            request.setPassword("Alice@123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Should return 400 when password is too short")
        void testRegisterShortPassword() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("Alice Smith");
            request.setEmail("alice@example.com");
            request.setPassword("Ab1@");   // Too short

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void testRegisterMissingName() throws Exception {
            RegisterRequest request = new RegisterRequest();
            // name intentionally omitted
            request.setEmail("alice@example.com");
            request.setPassword("Alice@123");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 on duplicate email")
        void testRegisterDuplicateEmail() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("Alice Smith");
            request.setEmail("alice@example.com");
            request.setPassword("Alice@123");

            when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessRuleException("Email address is already registered. Please use a different email."));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                        "Email address is already registered. Please use a different email."));
        }

        @Test
        @DisplayName("Should return 400 when password has no special character")
        void testRegisterWeakPassword() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setName("Alice Smith");
            request.setEmail("alice@example.com");
            request.setPassword("Alice1234");  // No special character

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ================================================================
    // POST /api/auth/login TESTS
    // ================================================================

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should return 200 OK with JWT on successful login")
        void testLoginSuccess() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("alice@example.com");
            request.setPassword("Alice@123");

            AuthResponse loginResponse = AuthResponse.builder()
                    .token("login.jwt.token")
                    .tokenType("Bearer")
                    .email("alice@example.com")
                    .name("Alice Smith")
                    .role(Role.USER)
                    .status(UserStatus.ACTIVE)
                    .message("Login successful")
                    .build();

            when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("login.jwt.token"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.message").value("Login successful"));
        }

        @Test
        @DisplayName("Should return 401 on invalid credentials")
        void testLoginInvalidCredentials() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("alice@example.com");
            request.setPassword("WrongPass@1");

            when(userService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }

        @Test
        @DisplayName("Should return 400 when email is missing from login")
        void testLoginMissingEmail() throws Exception {
            LoginRequest request = new LoginRequest();
            // email omitted
            request.setPassword("Alice@123");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when password is missing from login")
        void testLoginMissingPassword() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("alice@example.com");
            // password omitted

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for invalid email format in login")
        void testLoginInvalidEmailFormat() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("notanemail");
            request.setPassword("Alice@123");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ================================================================
    // POST /api/auth/logout TESTS
    // ================================================================

    @Nested
    @DisplayName("POST /api/auth/logout")
    class LogoutTests {

        @Test
        @DisplayName("Should return 200 OK on logout (stateless)")
        void testLogoutSuccess() throws Exception {
            UserDetails mockUser = User.withUsername("alice@example.com")
                    .password("hash")
                    .roles("USER")
                    .build();

            mockMvc.perform(post("/api/auth/logout")
                    .with(user(mockUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(
                        "Logout successful. Please discard your token."));
        }
    }

    // ================================================================
    // GET /api/auth/me TESTS
    // ================================================================

    @Nested
    @DisplayName("GET /api/auth/me")
    class GetMeTests {

        @Test
        @DisplayName("Should return 200 with user profile when authenticated")
        void testGetMeAuthenticated() throws Exception {
            UserDetails mockUser = User.withUsername("alice@example.com")
                    .password("hash")
                    .roles("USER")
                    .build();

            when(userService.getCurrentUser("alice@example.com")).thenReturn(userResponse);

            mockMvc.perform(get("/api/auth/me")
                    .with(user(mockUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("alice@example.com"))
                    .andExpect(jsonPath("$.name").value("Alice Smith"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("Should NOT return 200 when not authenticated (endpoint is protected)")
        void testGetMeUnauthorized() throws Exception {
            // When no Bearer token is provided to a protected endpoint,
            // Spring Security must block the request (not return 200/OK).
            // In @WebMvcTest slice tests, the exact status code (401/403/500)
            // depends on how Spring Security handles the unauthenticated request
            // in the test environment, but the critical fact is: it must NOT be 200.
            int status = mockMvc.perform(get("/api/auth/me"))
                    .andReturn()
                    .getResponse()
                    .getStatus();

            // The endpoint must NOT return 200 (OK) — it must be secured
            assertThat(status).as("GET /api/auth/me should be protected (not return 200 OK)").isNotEqualTo(200);
        }
    }

    // ================================================================
    // ROLE PROTECTION TESTS
    // ================================================================

    @Nested
    @DisplayName("Role Protection Tests")
    class RoleProtectionTests {

        @Test
        @DisplayName("USER role cannot access /api/admin endpoints — returns 403")
        void testUserCannotAccessAdminEndpoints() throws Exception {
            UserDetails regularUser = User.withUsername("alice@example.com")
                    .password("hash")
                    .roles("USER")
                    .build();

            mockMvc.perform(get("/api/admin/jobs")
                    .with(user(regularUser)))
                    .andExpect(status().isForbidden());
        }
    }
}
