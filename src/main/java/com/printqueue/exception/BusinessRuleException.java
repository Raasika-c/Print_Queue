package com.printqueue.exception;

/**
 * Thrown when a business rule is violated.
 * Examples: duplicate email, invalid state transition, cancellation of non-QUEUED job.
 * Maps to HTTP 400 Bad Request.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
