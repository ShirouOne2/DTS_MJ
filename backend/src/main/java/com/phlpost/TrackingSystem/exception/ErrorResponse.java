package com.phlpost.TrackingSystem.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details, // populated for validation errors, one entry per invalid field
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, List.of(), LocalDateTime.now());
    }

    public static ErrorResponse of(int status, String error, String message, List<String> details) {
        return new ErrorResponse(status, error, message, details, LocalDateTime.now());
    }
}
