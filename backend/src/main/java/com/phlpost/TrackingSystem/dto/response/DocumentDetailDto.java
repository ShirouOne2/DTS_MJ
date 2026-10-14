package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Backs GET /documents/{id} and DocumentView.vue. Everything a user needs
 * to see one document's full picture in one response — its files, its
 * current (and only its current — see currentTransaction) live routing
 * state, its comment thread, and its full immutable history.
 */
public record DocumentDetailDto(
        UUID id,
        String referenceNo,
        String subject,
        String remarks,
        String documentType,
        String status,
        boolean archived,
        DepartmentDto originDepartment,
        UserSummaryDto createdBy,
        LocalDate dateCreated,
        LocalTime timeCreated,
        LocalDateTime dateReleased,
        LocalDateTime updatedAt,

        List<DocumentFileDto> files,

        // The transaction currently driving `status` — null if the document
        // has no open transaction (e.g. Completed with nothing pending)
        RoutingTransactionDto currentTransaction,

        List<CommentDto> comments,

        // Full routing history, oldest first — from TRANSMITTALS
        List<TransmittalDto> history
) {}
