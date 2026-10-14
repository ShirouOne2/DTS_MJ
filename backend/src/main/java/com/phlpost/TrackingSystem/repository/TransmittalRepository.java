package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.Transmittal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Read/insert only — see Transmittal's class comment. Nothing here should
 * ever be used to build an update or delete against this table.
 */
public interface TransmittalRepository extends JpaRepository<Transmittal, UUID> {

    // The document's routing-history timeline, oldest first
    List<Transmittal> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);

    List<Transmittal> findByTransactionIdOrderByCreatedAtAsc(UUID transactionId);
}
