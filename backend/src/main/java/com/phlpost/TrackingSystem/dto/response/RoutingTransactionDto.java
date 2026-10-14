package com.phlpost.TrackingSystem.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoutingTransactionDto(
        UUID id,
        String type,   // initial_routing / forward / return
        String status, // open / closed
        String fromDepartmentName,
        int recipientCount,
        List<TransactionRecipientDto> recipients,
        List<ActionNeededDto> actionsNeeded,
        LocalDateTime createdAt
) {}
