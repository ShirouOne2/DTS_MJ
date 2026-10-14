package com.phlpost.TrackingSystem.service;

import com.phlpost.TrackingSystem.entity.User;
import com.phlpost.TrackingSystem.exception.NotFoundException;
import com.phlpost.TrackingSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * TEMPORARY. Real auth (Section 6 of implementation-plan.md — JWT + Spring
 * Security) isn't built yet, so there's no logged-in user to attribute
 * drafts/comments/audit entries to. This returns the first USERS row as a
 * stand-in so the rest of the service layer can be built and tested now
 * instead of blocking on auth.
 *
 * Replace getCurrentUser() with a real implementation reading the
 * authenticated principal (e.g. from SecurityContextHolder) once JWT auth
 * exists — every other service in this codebase calls this method rather
 * than resolving the user itself, specifically so there's exactly one place
 * to change when that happens.
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        return userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "No users exist yet — seed at least one USERS row before using endpoints that need a current user."));
    }
}
