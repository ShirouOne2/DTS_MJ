package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransmittalDto(
        UUID id,
        UUID documentId,
        String documentReferenceNo,
        UUID transactionId,
        String action, // Created / Forwarded / Received / Returned / Resubmitted / Action Completed / Released / Completed / Forward Undone
        String fromDepartmentName,
        // Derived, not a stored column: null for events with no destination
        // (Received, Action Completed, Completed), the single department name
        // for a single-recipient transaction, or "N departments" for a
        // multi-recipient broadcast. TRANSMITTALS itself only stores
        // from_department_id — see ui-refactor-plan.md's Transmittals section
        // for why there's no single stored "to" column to pull this from directly.
        String recipientSummary,
        String remarks,
        LocalDateTime createdAt
) {}
