package com.phlpost.TrackingSystem.exception;

/** Maps to 404 in GlobalExceptionHandler. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
