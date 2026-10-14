package com.phlpost.TrackingSystem.dto.response;

import java.util.UUID;

public record DepartmentDto(
        UUID id,
        String name,
        String code
) {}
