package com.philpost.dts.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 20) @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "letters, numbers and dashes only") String code) {}
