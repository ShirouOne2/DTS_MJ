package com.philpost.dts.website.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.philpost.dts.website.entity.DocumentVersion;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {

    @Query("select v from DocumentVersion v join fetch v.uploadedBy "
         + "where v.document.documentId = :documentId order by v.versionNo desc")
    List<DocumentVersion> findByDocumentIdWithUploader(@Param("documentId") Long documentId);

    @Query("select coalesce(max(v.versionNo), 0) from DocumentVersion v where v.document.documentId = :documentId")
    int maxVersionNo(@Param("documentId") Long documentId);

    Optional<DocumentVersion> findByVersionIdAndDocument_DocumentId(Long versionId, Long documentId);
    @Query("select v from DocumentVersion v join fetch v.document "
     + "where v.sha256 = :sha256 and v.sizeBytes = :size order by v.uploadedAt desc")
        
    List<DocumentVersion> findDuplicates(@Param("sha256") String sha256, @Param("size") long size);
}