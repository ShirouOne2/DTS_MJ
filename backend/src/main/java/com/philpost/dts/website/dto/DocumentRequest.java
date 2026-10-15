package com.philpost.dts.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentRequest(
        @NotBlank @Size(max = 500) String title,
        @NotNull Long documentTypeId,
        @Size(max = 5000) String remarks) {}