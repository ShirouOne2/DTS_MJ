package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.Draft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DraftRepository extends JpaRepository<Draft, UUID> {

    List<Draft> findByUserId(UUID userId);

    List<Draft> findByDocumentId(UUID documentId);
}
