package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID documentId,
        String documentReferenceNo,
        String type,
        String title,
        String message,
        boolean read,
        LocalDateTime createdAt
) {}
