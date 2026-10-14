package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.response.NotificationDto;
import com.phlpost.TrackingSystem.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> getAll() {
        return notificationService.getForCurrentUser();
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
    }

    @PatchMapping("/read-all")
    public void markAllRead() {
        notificationService.markAllRead();
    }
}
