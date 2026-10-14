package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "actions_needed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionNeeded {

    public enum Status { PENDING, DONE }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private RoutingTransaction transaction;

    @Column(nullable = false, length = 500)
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private ActionCode actionCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_department_id", nullable = false)
    private Department assignedDepartment;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Status status = Status.PENDING;
}
