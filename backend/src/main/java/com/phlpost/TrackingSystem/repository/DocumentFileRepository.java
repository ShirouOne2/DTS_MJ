package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DocumentFileRepository extends JpaRepository<DocumentFile, UUID> {

    List<DocumentFile> findByDocumentId(UUID documentId);

    // Backs the month/day directory browsing — e.g. everything uploaded in July 2026
    List<DocumentFile> findByUploadedAtBetween(LocalDateTime start, LocalDateTime end);
}
