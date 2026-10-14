package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Used by getRecentDocuments(), getMyDocuments(), getArchivedDocuments()
 * in documents.js — deliberately lighter than DocumentDetailDto: no
 * recipients/actions/files/transmittal history, since a list row doesn't
 * render any of that.
 */
public record DocumentSummaryDto(
        UUID id,
        String referenceNo,
        String subject,
        String documentType,
        String status,
        boolean archived,
        String originDepartmentName,
        String createdByUsername,
        LocalDate dateCreated,
        LocalDateTime updatedAt
) {}
