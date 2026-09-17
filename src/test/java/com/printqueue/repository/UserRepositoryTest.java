package com.printqueue.repository;

import com.printqueue.entity.Role;
import com.printqueue.entity.User;
import com.printqueue.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Level 2 — Repository / Database Test
 * Tests UserRepository against H2 in-memory database.
 * Verifies: persistence, uniqueness, queries, constraints.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Alice Smith")
                .email("alice@example.com")
                .mobile("9876543210")
                .password("$2a$12$hashedpassword")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        adminUser = User.builder()
                .name("Bob Admin")
                .email("bob@admin.com")
                .mobile("1234567890")
                .password("$2a$12$adminhashedpassword")
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
    }

    // ----------------------------------------------------------------
    // Persistence Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should save and retrieve user correctly")
    void shouldSaveAndRetrieveUser() {
        User saved = userRepository.save(testUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice Smith");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should auto-generate id")
    void shouldAutoGenerateId() {
        User saved = userRepository.save(testUser);
        assertThat(saved.getId()).isGreaterThan(0);
    }

    // ----------------------------------------------------------------
    // findByEmail Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should find user by email")
    void shouldFindByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("alice@example.com");

        assertTrue(found.isPresent());
        assertThat(found.get().getName()).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyForUnknownEmail() {
        Optional<User> found = userRepository.findByEmail("unknown@example.com");
        assertFalse(found.isPresent());
    }

    // ----------------------------------------------------------------
    // existsByEmail Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should return true for existing email")
    void shouldReturnTrueForExistingEmail() {
        userRepository.save(testUser);
        assertTrue(userRepository.existsByEmail("alice@example.com"));
    }

    @Test
    @DisplayName("Should return false for non-existing email")
    void shouldReturnFalseForNonExistingEmail() {
        assertFalse(userRepository.existsByEmail("notregistered@example.com"));
    }

    // ----------------------------------------------------------------
    // Uniqueness Constraint Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmail() {
        userRepository.save(testUser);

        User duplicate = User.builder()
                .name("Duplicate User")
                .email("alice@example.com") // same email
                .password("$2a$12$anotherpassword")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(duplicate);
        });
    }

    // ----------------------------------------------------------------
    // findByStatus Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should find users by status")
    void shouldFindByStatus() {
        userRepository.save(testUser); // ACTIVE

        User inactiveUser = User.builder()
                .name("Inactive User")
                .email("inactive@example.com")
                .password("$2a$12$pw")
                .role(Role.USER)
                .status(UserStatus.INACTIVE)
                .build();
        userRepository.save(inactiveUser);

        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
        List<User> inactiveUsers = userRepository.findByStatus(UserStatus.INACTIVE);

        assertThat(activeUsers).hasSize(1);
        assertThat(inactiveUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    // ----------------------------------------------------------------
    // findByRole Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should find users by role")
    void shouldFindByRole() {
        userRepository.save(testUser);  // USER
        userRepository.save(adminUser); // ADMIN

        List<User> users = userRepository.findByRole(Role.USER);
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        assertThat(users).hasSize(1);
        assertThat(admins).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("alice@example.com");
        assertThat(admins.get(0).getEmail()).isEqualTo("bob@admin.com");
    }

    // ----------------------------------------------------------------
    // countByRole / countByStatus Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should count users by role correctly")
    void shouldCountByRole() {
        userRepository.save(testUser);
        userRepository.save(adminUser);

        assertThat(userRepository.countByRole(Role.USER)).isEqualTo(1);
        assertThat(userRepository.countByRole(Role.ADMIN)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should count users by status correctly")
    void shouldCountByStatus() {
        userRepository.save(testUser); // ACTIVE

        assertThat(userRepository.countByStatus(UserStatus.ACTIVE)).isEqualTo(1);
        assertThat(userRepository.countByStatus(UserStatus.INACTIVE)).isEqualTo(0);
    }

    // ----------------------------------------------------------------
    // findByEmailAndStatus Test
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Should find active user by email")
    void shouldFindActiveUserByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmailAndStatus("alice@example.com", UserStatus.ACTIVE);
        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Should not find inactive user by email when looking for ACTIVE")
    void shouldNotFindInactiveUserWhenLookingForActive() {
        User inactiveUser = User.builder()
                .name("Inactive User")
                .email("inactive@example.com")
                .password("$2a$12$pw")
                .role(Role.USER)
                .status(UserStatus.INACTIVE)
                .build();
        userRepository.save(inactiveUser);

        Optional<User> found = userRepository.findByEmailAndStatus("inactive@example.com", UserStatus.ACTIVE);
        assertFalse(found.isPresent());
    }

    // ----------------------------------------------------------------
    // Default values Tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Default role should be USER")
    void defaultRoleShouldBeUser() {
        User user = User.builder()
                .name("Default User")
                .email("default@example.com")
                .password("$2a$12$pw")
                .build();
        User saved = userRepository.save(user);
        assertThat(saved.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Default status should be ACTIVE")
    void defaultStatusShouldBeActive() {
        User user = User.builder()
                .name("Default User")
                .email("default@example.com")
                .password("$2a$12$pw")
                .build();
        User saved = userRepository.save(user);
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
