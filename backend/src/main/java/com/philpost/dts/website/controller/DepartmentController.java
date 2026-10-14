package com.philpost.dts.website.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.philpost.dts.website.dto.ActiveRequest;
import com.philpost.dts.website.dto.DepartmentDto;
import com.philpost.dts.website.dto.DepartmentRequest;
import com.philpost.dts.website.service.DepartmentService;

import jakarta.validation.Valid;

// controller/DepartmentController.java
@RestController
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // Any logged-in user: active departments, for the route builder later.
    @GetMapping("/departments")
    public List<DepartmentDto> listActive() {
        return departmentService.listActive();
    }

    // Admin only (the /admin/** rule in SecurityConfig).
    @GetMapping("/admin/departments")
    public List<DepartmentDto> listAll() {
        return departmentService.listAll();
    }

    @PostMapping("/admin/departments")
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto create(@Valid @RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @PutMapping("/admin/departments/{id}")
    public DepartmentDto update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @PatchMapping("/admin/departments/{id}/active")
    public DepartmentDto setActive(@PathVariable Long id, @RequestBody ActiveRequest request) {
        return departmentService.setActive(id, request.active());
    }
}