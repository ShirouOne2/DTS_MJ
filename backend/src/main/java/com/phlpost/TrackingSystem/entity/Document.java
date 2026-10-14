package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    public enum Status {
        INCOMING, ONGOING, FOR_REVISE, FORWARDED, RELEASED, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference_no", nullable = false, unique = true, length = 50)
    private String referenceNo;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(columnDefinition = "text")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_department_id", nullable = false)
    private Department originDepartment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(name = "document_type", nullable = false, length = 100)
    private String documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.INCOMING;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_folder_id")
    private ArchiveFolder archiveFolder;

    @Column(name = "date_created", nullable = false)
    @Builder.Default
    private LocalDate dateCreated = LocalDate.now();

    @Column(name = "time_created", nullable = false)
    @Builder.Default
    private LocalTime timeCreated = LocalTime.now();

    @Column(name = "date_released")
    private LocalDateTime dateReleased;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- Convenience reverse mappings (this entity is the aggregate root) ---

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutingTransaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
