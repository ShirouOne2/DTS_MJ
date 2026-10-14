package com.philpost.dts.website.dto;

public record UserInfoDto(Long id, String username, String fullName, String role,
                          Long departmentId, String departmentName) {}