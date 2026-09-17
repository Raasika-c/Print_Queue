package com.printqueue.controller;

import com.printqueue.dto.request.LoginRequest;
import com.printqueue.dto.request.RegisterRequest;
import com.printqueue.dto.response.AuthResponse;
import com.printqueue.dto.response.UserResponse;
import com.printqueue.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication controller — handles register, login, logout, and current user.
 *
 * Endpoints:
 *   POST /api/auth/register — Register new user (public)
 *   POST /api/auth/login    — Login, receive JWT (public)
 *   POST /api/auth/logout   — Logout (stateless: client discards token)
 *   GET  /api/auth/me       — Get authenticated user profile
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and profile endpoints")
public class AuthController {

    private final UserService userService;

    // ----------------------------------------------------------------
    // POST /api/auth/register
    // ----------------------------------------------------------------

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new USER account and returns a JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ----------------------------------------------------------------
    // POST /api/auth/login
    // ----------------------------------------------------------------

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email + password. Returns JWT token on success.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // POST /api/auth/logout
    // ----------------------------------------------------------------

    @PostMapping("/logout")
    @Operation(
        summary = "Logout",
        description = "Stateless JWT: client must discard the token. No server-side session to invalidate."
    )
    public ResponseEntity<Map<String, String>> logout() {
        // JWT is stateless — the client is responsible for discarding the token.
        // In a production system, implement token blacklisting with Redis.
        return ResponseEntity.ok(Map.of(
            "message", "Logout successful. Please discard your token.",
            "status", "SUCCESS"
        ));
    }

    // ----------------------------------------------------------------
    // GET /api/auth/me
    // ----------------------------------------------------------------

    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Returns the authenticated user's profile. Requires valid Bearer token.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
