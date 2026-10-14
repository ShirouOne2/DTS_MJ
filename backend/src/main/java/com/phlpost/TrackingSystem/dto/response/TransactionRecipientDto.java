package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRecipientDto(
        UUID id,
        String departmentName,
        String status, // pending / delivered / viewed / received
        LocalDateTime deliveredAt,
        LocalDateTime viewedAt,
        LocalDateTime receivedAt
) {}
