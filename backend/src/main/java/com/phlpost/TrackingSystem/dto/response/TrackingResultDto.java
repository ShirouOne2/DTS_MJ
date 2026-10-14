package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Backs GET /tracking/{referenceNo} — Tracking.vue. Deliberately narrower
 * than DocumentDetailDto: no remarks, no comments, no file list. A
 * reference-number lookup is closer to a public "where is this document"
 * check than an internal detail view, so it only exposes status + timeline.
 */
public record TrackingResultDto(
        UUID id,
        String referenceNo,
        String subject,
        String documentType,
        String status,
        LocalDate dateCreated,
        List<TransmittalDto> history
) {}
