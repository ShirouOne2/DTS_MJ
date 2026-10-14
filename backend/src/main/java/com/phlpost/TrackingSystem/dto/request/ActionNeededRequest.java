package com.phlpost.TrackingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ActionNeededRequest(
        @NotBlank String label,
        String code, // one of ACTION_CODES.code, e.g. "FYAA" — nullable, matches the entity's optional FK
        @NotNull UUID assignedDepartmentId,
        LocalDate dueDate
) {}
