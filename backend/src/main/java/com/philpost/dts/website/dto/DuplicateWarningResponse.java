package com.philpost.dts.website.dto;

import java.time.LocalDateTime;

public record DuplicateWarningResponse(int status, String error, String message, boolean duplicate,
                                       String scope, String referenceNo, Integer versionNo,
                                       LocalDateTime timestamp) {}