package com.printqueue.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User entity representing a system user.
 *
 * Fields per Phase 2 specification:
 *   id, name, email, mobile, password, role, status, createdAt, updatedAt
 *
 * DEMO ADMIN CREDENTIAL:
 *   Email   : admin@digitalprint.com
 *   Password: Admin@123
 *   NOTE    : This is a DEMO/SEED credential for academic demonstration only.
 *             Change immediately in any production deployment.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_users_email",  columnList = "email"),
        @Index(name = "idx_users_role",   columnList = "role"),
        @Index(name = "idx_users_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user (display purposes).
     * Min 2 chars, max 100 chars.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Unique email address — used as login credential.
     * Must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Mobile/phone number (optional, for notifications).
     * 10-digit numeric string.
     */
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Mobile number must be exactly 10 digits"
    )
    @Column(name = "mobile", length = 10)
    private String mobile;

    /**
     * BCrypt-hashed password.
     * Plain-text passwords are NEVER stored.
     * BCrypt strength: 12 (configured in SecurityConfig).
     */
    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * Role: USER (default) or ADMIN.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * Account status: ACTIVE (default) or INACTIVE.
     * INACTIVE users cannot log in.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Timestamp when the account was created. Set automatically.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of last update. Set automatically on every save.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Convenience helpers ----

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
}
