package com.phlpost.TrackingSystem.service;

import com.phlpost.TrackingSystem.dto.response.NotificationDto;
import com.phlpost.TrackingSystem.entity.Notification;
import com.phlpost.TrackingSystem.entity.User;
import com.phlpost.TrackingSystem.exception.NotFoundException;
import com.phlpost.TrackingSystem.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public NotificationService(NotificationRepository notificationRepository, CurrentUserService currentUserService) {
        this.notificationRepository = notificationRepository;
        this.currentUserService = currentUserService;
    }

    public List<NotificationDto> getForCurrentUser() {
        User currentUser = currentUserService.getCurrentUser();
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void markRead(UUID id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found: " + id));
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllRead() {
        User currentUser = currentUserService.getCurrentUser();
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(currentUser.getId());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getDocument() != null ? n.getDocument().getId() : null,
                n.getDocument() != null ? n.getDocument().getReferenceNo() : null,
                n.getType(), n.getTitle(), n.getMessage(), n.isRead(), n.getCreatedAt()
        );
    }
}
