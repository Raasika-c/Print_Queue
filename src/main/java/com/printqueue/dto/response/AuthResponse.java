package com.printqueue.dto.response;

import com.printqueue.entity.Role;
import com.printqueue.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response body returned after successful login or registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private String mobile;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private String message;
}
