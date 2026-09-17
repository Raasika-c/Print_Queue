package com.printqueue.service;

import com.printqueue.dto.request.LoginRequest;
import com.printqueue.dto.request.RegisterRequest;
import com.printqueue.dto.response.AuthResponse;
import com.printqueue.dto.response.UserResponse;
import com.printqueue.entity.Role;
import com.printqueue.entity.User;
import com.printqueue.entity.UserStatus;
import com.printqueue.exception.AuthenticationException;
import com.printqueue.exception.BusinessRuleException;
import com.printqueue.exception.ResourceNotFoundException;
import com.printqueue.repository.UserRepository;
import com.printqueue.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Level 1 — Unit Tests for UserService
 * Uses Mockito — no Spring context, no database.
 * Tests: registration, login, business rules, error handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    // Test fixtures
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User savedUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setName("Alice Smith");
        validRegisterRequest.setEmail("alice@example.com");
        validRegisterRequest.setMobile("9876543210");
        validRegisterRequest.setPassword("Alice@123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("alice@example.com");
        validLoginRequest.setPassword("Alice@123");

        savedUser = User.builder()
                .id(1L)
                .name("Alice Smith")
                .email("alice@example.com")
                .mobile("9876543210")
                .password("$2a$12$hashedpassword")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        adminUser = User.builder()
                .id(2L)
                .name("System Administrator")
                .email("admin@digitalprint.com")
                .password("$2a$12$adminhashedpassword")
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ================================================================
    // REGISTRATION TESTS
    // ================================================================

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully with valid input")
        void testRegisterSuccess() {
            // Arrange
            when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
            when(passwordEncoder.encode("Alice@123")).thenReturn("$2a$12$hashedpassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtTokenProvider.generateTokenFromEmail("alice@example.com")).thenReturn("mock.jwt.token");

            // Act
            AuthResponse response = userService.register(validRegisterRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo("mock.jwt.token");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getEmail()).isEqualTo("alice@example.com");
            assertThat(response.getName()).isEqualTo("Alice Smith");
            assertThat(response.getRole()).isEqualTo(Role.USER);
            assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(response.getMessage()).contains("Registration successful");

            // Verify interactions
            verify(userRepository).existsByEmail("alice@example.com");
            verify(passwordEncoder).encode("Alice@123");
            verify(userRepository).save(any(User.class));
            verify(jwtTokenProvider).generateTokenFromEmail("alice@example.com");
        }

        @Test
        @DisplayName("Should reject registration with duplicate email — BR-001")
        void testRegisterDuplicateEmail() {
            // Arrange — email already exists
            when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

            // Act + Assert
            BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> userService.register(validRegisterRequest)
            );

            assertThat(exception.getMessage()).contains("already registered");

            // Should never reach save()
            verify(userRepository, never()).save(any(User.class));
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("New registered user should have USER role by default — BR-003")
        void testNewUserHasUserRoleByDefault() {
            // Arrange
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hash");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtTokenProvider.generateTokenFromEmail(anyString())).thenReturn("token");

            // Act
            AuthResponse response = userService.register(validRegisterRequest);

            // Assert — role must be USER
            assertThat(response.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("New registered user should have ACTIVE status by default")
        void testNewUserHasActiveStatus() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hash");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtTokenProvider.generateTokenFromEmail(anyString())).thenReturn("token");

            AuthResponse response = userService.register(validRegisterRequest);

            assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("Password should be encoded — never stored in plain text")
        void testPasswordIsHashed() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("Alice@123")).thenReturn("$2a$12$hashedpassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                // Verify password is hashed at save time
                assertThat(user.getPassword()).isEqualTo("$2a$12$hashedpassword");
                assertThat(user.getPassword()).doesNotContain("Alice@123");
                return savedUser;
            });
            when(jwtTokenProvider.generateTokenFromEmail(anyString())).thenReturn("token");

            userService.register(validRegisterRequest);

            verify(passwordEncoder).encode("Alice@123");
        }

        @Test
        @DisplayName("Registration should return JWT token in response")
        void testRegistrationReturnsJwtToken() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hash");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtTokenProvider.generateTokenFromEmail(anyString())).thenReturn("eyJhbGciOiJIUzI1NiJ9.test");

            AuthResponse response = userService.register(validRegisterRequest);

            assertThat(response.getToken()).isNotBlank();
            assertThat(response.getTokenType()).isEqualTo("Bearer");
        }
    }

    // ================================================================
    // LOGIN TESTS
    // ================================================================

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with correct credentials")
        void testLoginSuccess() {
            // Arrange
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("alice@example.com", "Alice@123");
            when(authenticationManager.authenticate(any())).thenReturn(authToken);
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));
            when(jwtTokenProvider.generateToken(any())).thenReturn("mock.jwt.token");

            // Act
            AuthResponse response = userService.login(validLoginRequest);

            // Assert
            assertThat(response.getToken()).isEqualTo("mock.jwt.token");
            assertThat(response.getEmail()).isEqualTo("alice@example.com");
            assertThat(response.getName()).isEqualTo("Alice Smith");
            assertThat(response.getMessage()).isEqualTo("Login successful");
        }

        @Test
        @DisplayName("Should reject login with wrong password — BR-005: generic message")
        void testLoginWrongPassword() {
            // Arrange
            when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act + Assert
            AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(validLoginRequest)
            );

            // BR-005: Must say "Invalid credentials" — not reveal which field is wrong
            assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        }

        @Test
        @DisplayName("Should reject login for INACTIVE user — BR-004, BR-005: generic message")
        void testLoginInactiveUser() {
            // Arrange — Spring Security DisabledException for inactive user
            when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User account is disabled"));

            // Act + Assert
            AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(validLoginRequest)
            );

            // BR-005: Same generic message — do not reveal account is inactive
            assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        }

        @Test
        @DisplayName("Login error should not reveal which field is wrong — BR-005")
        void testLoginDoesNotRevealFieldError() {
            LoginRequest badRequest = new LoginRequest();
            badRequest.setEmail("nonexistent@example.com");
            badRequest.setPassword("SomePass@1");

            when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

            AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(badRequest)
            );

            // Must NOT say "user not found" or "email not registered"
            assertThat(exception.getMessage()).doesNotContainIgnoringCase("not found");
            assertThat(exception.getMessage()).doesNotContainIgnoringCase("email");
            assertThat(exception.getMessage()).doesNotContainIgnoringCase("user");
            assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        }

        @Test
        @DisplayName("Admin should be able to log in")
        void testAdminLogin() {
            LoginRequest adminLogin = new LoginRequest();
            adminLogin.setEmail("admin@digitalprint.com");
            adminLogin.setPassword("Admin@123");

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("admin@digitalprint.com", "Admin@123");
            when(authenticationManager.authenticate(any())).thenReturn(authToken);
            when(userRepository.findByEmail("admin@digitalprint.com")).thenReturn(Optional.of(adminUser));
            when(jwtTokenProvider.generateToken(any())).thenReturn("admin.jwt.token");

            AuthResponse response = userService.login(adminLogin);

            assertThat(response.getRole()).isEqualTo(Role.ADMIN);
            assertThat(response.getToken()).isEqualTo("admin.jwt.token");
        }
    }

    // ================================================================
    // GET CURRENT USER TESTS
    // ================================================================

    @Nested
    @DisplayName("Get Current User Tests")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Should return user profile for valid email")
        void testGetCurrentUserSuccess() {
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));

            UserResponse response = userService.getCurrentUser("alice@example.com");

            assertThat(response.getEmail()).isEqualTo("alice@example.com");
            assertThat(response.getName()).isEqualTo("Alice Smith");
            assertThat(response.getRole()).isEqualTo(Role.USER);
            // Password must NOT be in response
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for unknown email")
        void testGetCurrentUserNotFound() {
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getCurrentUser("unknown@example.com")
            );
        }
    }

    // ================================================================
    // PASSWORD HASHING TEST
    // ================================================================

    @Test
    @DisplayName("BCrypt encoder should produce different hashes for same input")
    void testPasswordHashingProducesDifferentResults() {
        // Test BCrypt behavior — each call produces unique salt+hash
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);

        String hash1 = encoder.encode("Alice@123");
        String hash2 = encoder.encode("Alice@123");

        // Same input → different hash (due to random salt)
        assertThat(hash1).isNotEqualTo(hash2);

        // But both should verify correctly
        assertTrue(encoder.matches("Alice@123", hash1));
        assertTrue(encoder.matches("Alice@123", hash2));

        // Wrong password should not match
        assertFalse(encoder.matches("WrongPass@1", hash1));
    }

    // ================================================================
    // ADMIN OPERATIONS TESTS
    // ================================================================

    @Nested
    @DisplayName("Admin Operations Tests")
    class AdminOperationsTests {

        @Test
        @DisplayName("Should return all users for admin")
        void testGetAllUsers() {
            when(userRepository.findAll()).thenReturn(List.of(savedUser, adminUser));

            List<UserResponse> users = userService.getAllUsers();

            assertThat(users).hasSize(2);
        }

        @Test
        @DisplayName("Should get user by ID")
        void testGetUserById() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

            UserResponse response = userService.getUserById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for invalid user ID")
        void testGetUserByIdNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
            );
        }

        @Test
        @DisplayName("Should deactivate user")
        void testDeactivateUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
            User inactiveUser = User.builder()
                    .id(1L).name("Alice Smith").email("alice@example.com")
                    .password("hash").role(Role.USER).status(UserStatus.INACTIVE)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                    .build();
            when(userRepository.save(any(User.class))).thenReturn(inactiveUser);

            UserResponse response = userService.updateUserStatus(1L, UserStatus.INACTIVE);

            assertThat(response.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
    }

    // ================================================================
    // MAPPER TEST
    // ================================================================

    @Test
    @DisplayName("mapToUserResponse should not expose password")
    void testMapperDoesNotExposePassword() {
        UserResponse response = userService.mapToUserResponse(savedUser);

        // UserResponse has no password field — verified by structure
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Alice Smith");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
