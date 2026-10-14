package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ActionNeededDto(
        UUID id,
        String label,
        String code,
        String assignedDepartmentName,
        LocalDate dueDate,
        String status
) {}
