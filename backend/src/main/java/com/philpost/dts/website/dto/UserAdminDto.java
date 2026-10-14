package com.philpost.dts.website.dto;

import java.time.LocalDateTime;

public record UserAdminDto(Long id, String username, String firstName, String lastName, String email,
                           String role, Long departmentId, String departmentName,
                           boolean active, LocalDateTime lastLoginAt) {}