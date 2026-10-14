package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Security/access trail — logins, views, downloads, uploads, edits,
 * archiving. NOT a routing change log — see Transmittal for that.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    public enum Action { LOGIN, LOGOUT, VIEWED, DOWNLOADED, UPLOADED, EDITED, ARCHIVED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_user_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Action action;

    @Column(name = "entity_type", length = 30)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    // Before/after values — only populated for EDITED
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> changes;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "is_undone", nullable = false)
    @Builder.Default
    private boolean undone = false;

    @Column(name = "undoable_until")
    private LocalDateTime undoableUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
