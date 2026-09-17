package com.printqueue.exception;

/**
 * Thrown when authentication fails (wrong credentials, inactive account).
 * Maps to HTTP 401 Unauthorized.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
