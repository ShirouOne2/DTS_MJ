package com.philpost.dts.website.dto;

import java.time.LocalDateTime;

public record DocumentSummaryDto(Long id, String referenceNo, String title, String documentType,
                                 String status, String ownerName, String departmentName,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {}
