package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.UndoLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UndoLogRepository extends JpaRepository<UndoLog, UUID> {

    // Validates an undo request: belongs to this actor, not already undone,
    // and still inside the window — the single check the undo endpoint needs
    Optional<UndoLog> findByIdAndActorIdAndUndoneFalseAndUndoableUntilAfter(
            UUID id, UUID actorUserId, LocalDateTime now);
}
