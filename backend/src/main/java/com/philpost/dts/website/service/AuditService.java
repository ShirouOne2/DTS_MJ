package com.philpost.dts.website.service;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.philpost.dts.website.dto.AuditLogDto;
import com.philpost.dts.website.dto.PageResponse;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.AuditLog;
import com.philpost.dts.website.repository.AuditLogRepository;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    /** Joins the caller's transaction. The actor is whoever is logged in right now. */
    @Transactional
    public void log(AuditAction action, String entityType, Long entityId, String details) {
        save(currentUsername(), action, entityType, entityId, details);
    }

    /** For events where nobody is authenticated yet (a successful login). */
    @Transactional
    public void logAs(String username, AuditAction action, String entityType, Long entityId, String details) {
        save(username, action, entityType, entityId, details);
    }

    /** Own transaction: still saved even if the caller rolls back (failed logins). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logIndependent(String username, AuditAction action, String entityType, Long entityId, String details) {
        save(username, action, entityType, entityId, details);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogDto> search(AuditAction action, String username, LocalDate from, LocalDate to,
                                            int page, int size) {
        Page<AuditLog> result = repository.search(
                action,
                username == null || username.isBlank() ? null : username.trim(),
                from != null ? from.atStartOfDay() : null,
                to != null ? to.plusDays(1).atStartOfDay() : null,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100)));
        return new PageResponse<>(result.getContent().stream().map(this::toDto).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private void save(String username, AuditAction action, String entityType, Long entityId, String details) {
        HttpServletRequest request = currentRequest();
        repository.save(AuditLog.builder()
                .actorUsername(cut(username, 100))
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(cut(details, 1000))
                .ipAddress(request != null ? cut(request.getRemoteAddr(), 45) : null)
                .userAgent(request != null ? cut(request.getHeader("User-Agent"), 255) : null)
                .build());
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) return null;
        return auth.getName();
    }

    private HttpServletRequest currentRequest() {
        return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs
                ? attrs.getRequest() : null;
    }

    private String cut(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }

    private AuditLogDto toDto(AuditLog a) {
        return new AuditLogDto(a.getAuditLogId(), a.getActorUsername(), a.getAction().name(), a.getEntityType(),
                a.getEntityId(), a.getDetails(), a.getIpAddress(), a.getCreatedAt());
    }
}