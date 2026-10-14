package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_recipients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecipient {

    public enum Status { PENDING, DELIVERED, VIEWED, RECEIVED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private RoutingTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;
}
