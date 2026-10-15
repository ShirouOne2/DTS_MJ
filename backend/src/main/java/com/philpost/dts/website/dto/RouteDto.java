package com.philpost.dts.website.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RouteDto(Long id, int roundNo, String routeType, String status, int versionNo,
                       String submittedByName, String remarks,
                       LocalDateTime startedAt, LocalDateTime endedAt, List<StepDto> steps) {}