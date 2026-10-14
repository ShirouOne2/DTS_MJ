package com.phlpost.TrackingSystem.dto.response;

import java.util.UUID;

/**
 * Deliberately excludes passwordHash and role — this is what gets embedded
 * in other DTOs (e.g. "created by") where only a name is needed. If a
 * screen needs role, add it explicitly rather than reusing this type,
 * so it stays obvious this shape is safe to send anywhere.
 */
public record UserSummaryDto(
        UUID id,
        String username,
        String departmentName
) {}
