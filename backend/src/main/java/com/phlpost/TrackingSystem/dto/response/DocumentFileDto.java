package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentFileDto(
        UUID id,
        String fileName,
        String filePath,
        int fileSize,
        LocalDateTime uploadedAt
) {}
