package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.response.AuditLogDto;
import com.phlpost.TrackingSystem.entity.AuditLog;
import com.phlpost.TrackingSystem.repository.AuditLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * TODO once real auth exists (Section 6): gate this whole controller to
 * role = 'admin' — this is a security/access trail, not something a regular
 * staff account should be able to browse. No access control at all right now.
 */
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public List<AuditLogDto> getAll() {
        return auditLogRepository.findAll().stream()
                .sorted(Comparator.comparing(AuditLog::getCreatedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    private AuditLogDto toDto(AuditLog log) {
        return new AuditLogDto(
                log.getId(),
                log.getActor().getUsername(),
                log.getAction().name().toLowerCase(),
                log.getEntityType(),
                log.getEntityId(),
                log.getIpAddress(),
                log.getCreatedAt()
        );
    }
}
