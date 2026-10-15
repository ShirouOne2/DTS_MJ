package com.philpost.dts.website.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentDetailDto(Long id, String referenceNo, String title, Long documentTypeId,
                                String documentType, String remarks, String status,
                                Long departmentId, String departmentName,
                                String createdByName, String createdByUsername,
                                LocalDateTime createdAt, LocalDateTime updatedAt,
                                List<String> allowedActions, List<VersionDto> versions,
                                List<RouteDto> routes) {}