package com.philpost.dts.website.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.philpost.dts.website.dto.AuditLogDto;
import com.philpost.dts.website.dto.PageResponse;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.service.AuditService;

// controller/AuditLogController.java  (under /admin/**, so admin-only already)
@RestController
@RequestMapping("/admin/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public PageResponse<AuditLogDto> search(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        return auditService.search(action, username, from, to, page, size);
    }
}