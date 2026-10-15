package com.philpost.dts.website.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RouteStepRequest(@NotNull Long departmentId, String actionCode,
                               @Size(max = 500) String instruction, LocalDate dueDate) {}
