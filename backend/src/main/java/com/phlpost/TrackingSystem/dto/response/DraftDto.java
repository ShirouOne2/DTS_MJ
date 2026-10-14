package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Backs GET /drafts/{id} — resuming one specific draft. formData is
 * returned as-is (whatever shape the frontend saved it in); this DTO
 * doesn't validate or reshape it, since a draft is explicitly meant to
 * hold incomplete/in-progress data that wouldn't pass CreateDocumentRequest's
 * validation yet.
 */
public record DraftDto(
        UUID id,
        UUID documentId,
        Map<String, Object> formData,
        LocalDateTime lastSavedAt
) {}
