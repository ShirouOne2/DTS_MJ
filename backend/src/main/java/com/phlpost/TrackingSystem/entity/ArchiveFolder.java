package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UI-only organization folder. Has no bearing on where a file
 * physically lives on disk — see DocumentFile.filePath for that.
 */
@Entity
@Table(name = "archive_folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private ArchiveFolder parentFolder;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
