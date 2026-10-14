package com.philpost.dts.website.dto;

import java.time.LocalDateTime;

public record AuditLogDto(Long id, String actorUsername, String action, String entityType,
                          Long entityId, String details, String ipAddress, LocalDateTime createdAt) {}
