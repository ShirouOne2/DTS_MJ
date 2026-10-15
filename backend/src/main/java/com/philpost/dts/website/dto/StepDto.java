package com.philpost.dts.website.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StepDto(Long id, int stepOrder, Long departmentId, String departmentName,
                      String actionCode, String actionLabel, boolean requiresSignature,
                      String instruction, LocalDate dueDate, String status, String decision,
                      LocalDateTime activatedAt, String receivedByName, LocalDateTime receivedAt,
                      String decidedByName, LocalDateTime decidedAt, String decisionRemarks,
                      Integer signedVersionNo, List<String> allowedActions) {}