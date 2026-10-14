package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.TransactionRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRecipientRepository extends JpaRepository<TransactionRecipient, UUID> {

    List<TransactionRecipient> findByTransactionId(UUID transactionId);

    // "Has everyone received it yet?" — the transaction-closing guard from Phase 2
    boolean existsByTransactionIdAndStatusNot(UUID transactionId, TransactionRecipient.Status status);
}
