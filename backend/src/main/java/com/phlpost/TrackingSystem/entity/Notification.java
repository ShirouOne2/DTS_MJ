package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    // Generic reference — what triggered this notification (e.g. "TRANSMITTALS", "COMMENTS")
    @Column(name = "entity_type", length = 30)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
