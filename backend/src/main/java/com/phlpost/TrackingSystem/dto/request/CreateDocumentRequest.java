package com.phlpost.TrackingSystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * Matches the payload shape documented in documents.js's createDocument():
 *   { subject, remarks, actionsNeeded, recipients, departmentComments }
 *
 * One addition: documentType. The frontend comment doesn't list it, but
 * Document.documentType is NOT NULL at the entity level — confirm with
 * whoever owns the upload form whether it's a dropdown that isn't wired
 * into the payload yet, and add it there if so. Left required here since
 * the database will reject the insert without it either way.
 */
public record CreateDocumentRequest(
        @NotBlank String subject,
        String remarks,
        @NotBlank String documentType,
        List<String> actionsNeeded,        // action codes, e.g. ["FYAA", "FCOM"] — may be empty
        @NotEmpty List<UUID> recipients,
        String departmentComments
) {}
