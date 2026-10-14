package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "drafts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Draft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Null if this is a draft of a brand-new, not-yet-submitted document;
    // set if it's a revision of an existing one.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "form_data", nullable = false, columnDefinition = "json")
    private Map<String, Object> formData;

    @Column(name = "last_saved_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastSavedAt = LocalDateTime.now();
}
