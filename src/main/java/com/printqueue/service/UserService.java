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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService — all business logic for user management and authentication.
 *
 * Business rules enforced here:
 *  BR-001: Email must be unique
 *  BR-002: Password must meet complexity requirements (enforced via DTO validation)
 *  BR-003: Default role is USER
 *  BR-004: INACTIVE users cannot log in
 *  BR-005: Failed login does NOT reveal which field is wrong
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // ================================================================
    // REGISTRATION
    // ================================================================

    /**
     * Register a new user.
     *
     * Steps:
     *  1. Check email uniqueness (BR-001)
     *  2. Hash the password (BCrypt-12)
     *  3. Create user with role=USER, status=ACTIVE (BR-003)
     *  4. Save to database
     *  5. Generate JWT token
     *  6. Return AuthResponse
     *
     * @param request - Registration form data (validated by DTO constraints)
     * @return AuthResponse with token and user info
     * @throws BusinessRuleException if email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        // BR-001: Email must be unique
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email address is already registered. Please use a different email.");
        }

        // Hash password (BCrypt-12)
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Build user entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .mobile(request.getMobile())
                .password(hashedPassword)
                .role(Role.USER)            // BR-003: Default role is USER
                .status(UserStatus.ACTIVE)  // New accounts are ACTIVE
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: email={}, id={}", saved.getEmail(), saved.getId());

        // Generate JWT
        String token = jwtTokenProvider.generateTokenFromEmail(saved.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .mobile(saved.getMobile())
                .role(saved.getRole())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .message("Registration successful. Welcome to Digital Print Queue!")
                .build();
    }

    // ================================================================
    // LOGIN
    // ================================================================

    /**
     * Authenticate user and return JWT token.
     *
     * Steps:
     *  1. Attempt Spring Security authentication (validates password + active state)
     *  2. On success, generate JWT
     *  3. On failure, throw generic AuthenticationException (BR-005: do not reveal which field failed)
     *
     * @param request - Login credentials
     * @return AuthResponse with token and user info
     * @throws AuthenticationException on wrong credentials or inactive account
     */
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().toLowerCase().trim(),
                            request.getPassword()
                    )
            );

            // Authentication succeeded — load full user entity
            User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                    .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

            // BR-004: Double-check account status (DisabledException should have been thrown above)
            if (!user.isActive()) {
                throw new AuthenticationException("Invalid credentials");
            }

            String token = jwtTokenProvider.generateToken(authentication);
            log.info("User logged in: email={}, id={}", user.getEmail(), user.getId());

            return AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .mobile(user.getMobile())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .message("Login successful")
                    .build();

        } catch (DisabledException ex) {
            // BR-005: Do not reveal reason — generic message
            log.warn("Login attempt for inactive account: {}", request.getEmail());
            throw new AuthenticationException("Invalid credentials");
        } catch (BadCredentialsException ex) {
            // BR-005: Do not reveal which field was wrong
            log.warn("Failed login attempt for: {}", request.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }
    }

    // ================================================================
    // GET CURRENT USER
    // ================================================================

    /**
     * Retrieve the authenticated user's profile.
     *
     * @param email - Email extracted from JWT token
     * @return UserResponse (no password exposed)
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    // ================================================================
    // ADMIN OPERATIONS
    // ================================================================

    /**
     * List all users (admin only).
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single user by ID (admin only).
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToUserResponse(user);
    }

    /**
     * Activate or deactivate a user account (admin only).
     */
    public UserResponse updateUserStatus(Long id, UserStatus newStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        UserStatus oldStatus = user.getStatus();
        user.setStatus(newStatus);
        User saved = userRepository.save(user);

        log.info("Admin updated user id={} status: {} -> {}", id, oldStatus, newStatus);
        return mapToUserResponse(saved);
    }

    // ================================================================
    // MAPPER
    // ================================================================

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
