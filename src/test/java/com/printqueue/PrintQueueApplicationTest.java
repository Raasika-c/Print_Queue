package com.printqueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Level 4 — Integration Test
 * Verifies Spring context loads completely with all beans wired correctly.
 * Uses H2 in-memory database (no MySQL required for this test).
 *
 * webEnvironment=NONE: avoids starting Tomcat, faster, and avoids
 * scheduling pool size conflicts. We only need the bean context to verify
 * correct wiring.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("Spring Application Context Load Test")
class PrintQueueApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Core beans are registered in context")
    void coreBeansShouldBeRegistered() {
        assertThat(applicationContext.containsBean("userService")).isTrue();
        assertThat(applicationContext.containsBean("userRepository")).isTrue();
        assertThat(applicationContext.containsBean("authController")).isTrue();
        assertThat(applicationContext.containsBean("jwtTokenProvider")).isTrue();
        assertThat(applicationContext.containsBean("securityConfig")).isTrue();
        assertThat(applicationContext.containsBean("dataInitializer")).isTrue();
    }

    @Test
    @DisplayName("Password encoder bean is configured")
    void passwordEncoderShouldBeAvailable() {
        assertThat(applicationContext.containsBean("passwordEncoder")).isTrue();
    }
}
