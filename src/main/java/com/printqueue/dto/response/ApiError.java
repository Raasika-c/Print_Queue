package com.printqueue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API error response returned by GlobalExceptionHandler.
 *
 * Format:
 * {
 *   "timestamp": "2026-09-17T18:00:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Email already registered",
 *   "path": "/api/auth/register"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
