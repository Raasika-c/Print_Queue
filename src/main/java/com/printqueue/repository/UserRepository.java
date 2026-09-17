package com.printqueue.repository;

import com.printqueue.entity.Role;
import com.printqueue.entity.User;
import com.printqueue.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity — all DB operations for users table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find user by email (login credential). */
    Optional<User> findByEmail(String email);

    /** Check if an email is already registered. */
    boolean existsByEmail(String email);

    /** Find all active users. */
    List<User> findByStatus(UserStatus status);

    /** Find all users with a specific role. */
    List<User> findByRole(Role role);

    /** Find active user by email (for login — rejects INACTIVE). */
    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    /** Count users by role. */
    long countByRole(Role role);

    /** Count users by status. */
    long countByStatus(UserStatus status);

    /** Search users by name (case-insensitive). */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(String name);
}
