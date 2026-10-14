package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Optional<Document> findByReferenceNo(String referenceNo);

    // "My Docs" — schema doc's rule: WHERE status != 'Completed'
    List<Document> findByStatusNot(Document.Status status);

    // "Archived" — schema doc's rule: WHERE is_archived = true
    List<Document> findByArchivedTrue();

    List<Document> findByOriginDepartmentId(UUID departmentId);

    List<Document> findByCreatedById(UUID userId);

    List<Document> findByArchiveFolderId(UUID archiveFolderId);

    // Dashboard counters: COUNT(*) ... GROUP BY status
    @Query("SELECT d.status AS status, COUNT(d) AS count FROM Document d GROUP BY d.status")
    List<StatusCount> countByStatusGrouped();

    interface StatusCount {
        Document.Status getStatus();
        Long getCount();
    }
}
