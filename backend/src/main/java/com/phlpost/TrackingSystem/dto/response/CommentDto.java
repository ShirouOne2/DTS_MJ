package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        String authorUsername,
        String message,
        LocalDateTime createdAt
) {}
