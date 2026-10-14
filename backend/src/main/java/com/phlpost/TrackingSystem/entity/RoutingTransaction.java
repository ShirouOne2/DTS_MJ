package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The mutable live state of a single dispatch (initial routing, a forward,
 * or a return). Parent of TransactionRecipient and ActionNeeded.
 *
 * recipientCount drives the single-vs-multiple status branching:
 * single recipient = a chain (can return, can forward onward again).
 * multiple recipients = one-way broadcast (receive only, no return/forward path).
 */
@Entity
@Table(name = "routing_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutingTransaction {

    public enum Type { INITIAL_ROUTING, FORWARD, RETURN }
    public enum Status { OPEN, CLOSED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_department_id", nullable = false)
    private Department fromDepartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type;

    @Column(name = "recipient_count", nullable = false)
    @Builder.Default
    private int recipientCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Status status = Status.OPEN;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransactionRecipient> recipients = new ArrayList<>();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ActionNeeded> actionsNeeded = new ArrayList<>();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
