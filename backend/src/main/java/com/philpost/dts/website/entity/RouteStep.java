package com.philpost.dts.website.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "route_steps", indexes = {
    @Index(name = "idx_steps_department_status", columnList = "department_id, status"),
    @Index(name = "idx_steps_route", columnList = "route_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long stepId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_code", nullable = false)
    private ActionCode actionCode;

    @Column(length = 500)
    private String instruction;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StepStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StepDecision decision;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by_user_id")
    private User receivedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by_user_id")
    private User decidedBy;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "decision_remarks", length = 1000)
    private String decisionRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_version_id")
    private DocumentVersion resultVersion;   // was: signedVersion
}