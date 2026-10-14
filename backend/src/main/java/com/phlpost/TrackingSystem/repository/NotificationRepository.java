package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Notification.isRead()'s backing field is "read" — property name Spring Data
    // resolves against is "Read", not "IsRead"
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);

    long countByUserIdAndReadFalse(UUID userId);
}
