package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * One row per user-facing operation (not per row changed) — a single
 * "Forward" click that touches several tables is still one UndoLog row.
 * snapshot shape: { "created_rows": [...], "updated_rows": [{"before":..,"after":..}] }
 */
@Entity
@Table(name = "undo_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UndoLog {

    public enum OperationType {
        FORWARD, RETURN, MARK_ACTION_DONE, EDIT_DOCUMENT, ADD_COMMENT, ARCHIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_user_id", nullable = false)
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 30)
    private OperationType operationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private Map<String, Object> snapshot;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "undoable_until", nullable = false)
    private LocalDateTime undoableUntil;

    @Column(name = "is_undone", nullable = false)
    @Builder.Default
    private boolean undone = false;

    @Column(name = "undone_at")
    private LocalDateTime undoneAt;
}
