package com.philpost.dts.website.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InboxItemDto(Long documentId, String referenceNo, String title, String documentType,
                           String originDepartment, String routeType, int roundNo,
                           Long stepId, String actionCode, String actionLabel, boolean requiresSignature,
                           String fileKind, String instruction, LocalDate dueDate, boolean overdue,
                           LocalDateTime activatedAt, LocalDateTime receivedAt, List<String> actions) {}