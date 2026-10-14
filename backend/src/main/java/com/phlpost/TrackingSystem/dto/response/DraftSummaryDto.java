package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Backs GET /drafts — the "resume where you left off" list. Deliberately
 * light: enough to render a card (what it's called, when it was last
 * saved), not the full form_data blob — that's only needed once a specific
 * draft is opened, not for every row in the list.
 */
public record DraftSummaryDto(
        UUID id,
        UUID documentId, // null = draft of a brand-new document; set = revision draft
        String subjectPreview, // pulled from form_data.subject, for display only
        LocalDateTime lastSavedAt
) {}
