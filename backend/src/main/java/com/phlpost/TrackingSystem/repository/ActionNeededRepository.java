package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.ActionNeeded;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActionNeededRepository extends JpaRepository<ActionNeeded, UUID> {

    List<ActionNeeded> findByTransactionId(UUID transactionId);

    List<ActionNeeded> findByAssignedDepartmentIdAndStatus(UUID departmentId, ActionNeeded.Status status);

    // "Is anything still pending?" — the other half of the transaction-closing guard
    boolean existsByTransactionIdAndStatus(UUID transactionId, ActionNeeded.Status status);
}
