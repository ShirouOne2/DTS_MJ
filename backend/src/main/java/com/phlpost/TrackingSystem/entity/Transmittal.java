package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable log — insert only, application code should never issue
 * an UPDATE or DELETE against this table. No setters are wired up
 * to a repository "save-for-update" path; treat every row as final
 * once persisted.
 */
@Entity
@Table(name = "transmittals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transmittal {

    public enum Action {
        CREATED, FORWARDED, RECEIVED, RETURNED, RESUBMITTED,
        ACTION_COMPLETED, RELEASED, COMPLETED, FORWARD_UNDONE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private RoutingTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_department_id")
    private Department fromDepartment;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
