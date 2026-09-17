package com.printqueue.exception;

/**
 * Thrown when a requested resource (user, job, printer) is not found.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " not found with id: " + id);
    }

    public ResourceNotFoundException(String resourceType, String field, String value) {
        super(resourceType + " not found with " + field + ": " + value);
    }
}
