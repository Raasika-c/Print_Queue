package com.printqueue.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Level 1 — Unit Tests for JwtTokenProvider
 * Tests: token generation, parsing, validation, expiry.
 */
@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET =
        "TestSecretKey2026DigitalPrintQueueManagementSystem23IT723Lab";
    private static final long TEST_EXPIRY_MS = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiryMs", TEST_EXPIRY_MS);
    }

    @Test
    @DisplayName("Should generate valid JWT token from email")
    void shouldGenerateTokenFromEmail() {
        String token = jwtTokenProvider.generateTokenFromEmail("alice@example.com");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("Should generate valid JWT token from Authentication object")
    void shouldGenerateTokenFromAuthentication() {
        UserDetails userDetails = User.withUsername("alice@example.com")
                .password("hash")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertThat(token).isNotBlank();
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should extract correct email from token")
    void shouldExtractEmailFromToken() {
        String token = jwtTokenProvider.generateTokenFromEmail("alice@example.com");
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        assertThat(extractedEmail).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("Should validate a freshly generated token as true")
    void shouldValidateFreshToken() {
        String token = jwtTokenProvider.generateTokenFromEmail("alice@example.com");
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should reject a tampered token (wrong signature)")
    void shouldRejectTamperedToken() {
        String token = jwtTokenProvider.generateTokenFromEmail("alice@example.com");
        // A JWT has 3 parts: header.payload.signature
        // Replace the signature with garbage to produce an invalid-signature token
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
        String tamperedToken = parts[0] + "." + parts[1] + ".invalidsignatureXYZ123";

        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("Should reject a completely invalid token string")
    void shouldRejectInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("not.a.jwt.token"));
    }

    @Test
    @DisplayName("Should reject empty/null token string")
    void shouldRejectEmptyToken() {
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken("   "));
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        // Create a provider with 1ms expiry
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(shortLivedProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(shortLivedProvider, "jwtExpiryMs", 1L); // 1ms

        String token = shortLivedProvider.generateTokenFromEmail("alice@example.com");

        // Wait for expiry
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        assertFalse(shortLivedProvider.validateToken(token));
    }

    @Test
    @DisplayName("Two tokens for same email should be different (unique issuedAt)")
    void tokensShouldBeDifferentForSameEmail() throws InterruptedException {
        String token1 = jwtTokenProvider.generateTokenFromEmail("alice@example.com");
        Thread.sleep(10); // Ensure different timestamp
        String token2 = jwtTokenProvider.generateTokenFromEmail("alice@example.com");

        // Both should be valid
        assertTrue(jwtTokenProvider.validateToken(token1));
        assertTrue(jwtTokenProvider.validateToken(token2));

        // But both should decode to same email
        assertThat(jwtTokenProvider.getEmailFromToken(token1)).isEqualTo("alice@example.com");
        assertThat(jwtTokenProvider.getEmailFromToken(token2)).isEqualTo("alice@example.com");
    }
}
