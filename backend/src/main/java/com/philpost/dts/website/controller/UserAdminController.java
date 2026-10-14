package com.philpost.dts.website.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.philpost.dts.website.dto.ActiveRequest;
import com.philpost.dts.website.dto.CreateUserRequest;
import com.philpost.dts.website.dto.ResetPasswordRequest;
import com.philpost.dts.website.dto.UpdateUserRequest;
import com.philpost.dts.website.dto.UserAdminDto;
import com.philpost.dts.website.service.UserAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserAdminService service;

    public UserAdminController(UserAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserAdminDto> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAdminDto create(@Valid @RequestBody CreateUserRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public UserAdminDto update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request,
                               Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/active")
    public UserAdminDto setActive(@PathVariable Long id, @RequestBody ActiveRequest request,
                                  Authentication authentication) {
        return service.setActive(id, request.active(), authentication.getName());
    }

    @PostMapping("/{id}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        service.resetPassword(id, request.newPassword());
    }
}