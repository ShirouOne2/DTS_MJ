package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByActorIdOrderByCreatedAtDesc(UUID actorUserId);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, UUID entityId);
}
