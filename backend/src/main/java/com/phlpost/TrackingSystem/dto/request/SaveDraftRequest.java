package com.phlpost.TrackingSystem.dto.request;

import java.util.Map;
import java.util.UUID;

/**
 * id is nullable — omit it to create a new draft, pass it back to update
 * the same one (matches saveDraft()'s upsert usage in api/drafts.js).
 */
public record SaveDraftRequest(
        UUID id,
        UUID documentId, // null = draft of a new document; set = revision draft
        Map<String, Object> formData
) {}
