package com.phlpost.TrackingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Backs POST /documents/{documentId}/remarks — matches addRemark(documentId, message)
 * in documents.js, which sends { message }.
 */
public record AddRemarkRequest(
        @NotBlank String message
) {}
