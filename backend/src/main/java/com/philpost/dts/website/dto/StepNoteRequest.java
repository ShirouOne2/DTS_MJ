package com.philpost.dts.website.dto;

import jakarta.validation.constraints.Size;

public record StepNoteRequest(@Size(max = 1000) String remarks) {}