package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.RoutingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoutingTransactionRepository extends JpaRepository<RoutingTransaction, UUID> {

    List<RoutingTransaction> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);

    // The transaction currently driving DOCUMENTS.status — status derivation reads this
    Optional<RoutingTransaction> findFirstByDocumentIdOrderByCreatedAtDesc(UUID documentId);

    Optional<RoutingTransaction> findFirstByDocumentIdAndStatusOrderByCreatedAtDesc(
            UUID documentId, RoutingTransaction.Status status);
}
