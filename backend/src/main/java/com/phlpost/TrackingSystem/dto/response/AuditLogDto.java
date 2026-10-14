package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Backs GET /audit-logs — the admin-only "File Integrity" page. Should be
 * gated to role = 'admin' at the controller level; this is a security/access
 * trail, not something a regular staff account should be able to browse.
 */
public record AuditLogDto(
        UUID id,
        String actorUsername,
        String action, // login / logout / viewed / downloaded / uploaded / edited / archived
        String entityType,
        UUID entityId,
        String ipAddress,
        LocalDateTime createdAt
) {}
