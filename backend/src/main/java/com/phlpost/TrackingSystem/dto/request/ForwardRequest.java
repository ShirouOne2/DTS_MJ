package com.phlpost.TrackingSystem.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * Backs POST /documents/{id}/forward. The service layer — not this DTO —
 * is responsible for rejecting a forward whose recipients list would make
 * this a 'return' onto a transaction that already had multiple recipients;
 * this shape doesn't know or care about that rule, it just carries the data.
 */
public record ForwardRequest(
        @NotEmpty List<UUID> recipients,   // department IDs
        List<String> actionsNeeded,        // action codes, e.g. ["FYAA", "FCOM"] — may be empty
        String remarks                      // optional note for the TRANSMITTALS row
) {}