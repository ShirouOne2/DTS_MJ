package com.phlpost.TrackingSystem.exception;

/**
 * Thrown for business-rule violations the service layer catches before they
 * become a confusing 500 — e.g. "can't return a multi-recipient transaction."
 * Maps to 409 Conflict in GlobalExceptionHandler.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
