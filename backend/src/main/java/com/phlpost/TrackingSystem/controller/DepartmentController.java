package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.response.DepartmentDto;
import com.phlpost.TrackingSystem.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .map(d -> new DepartmentDto(d.getId(), d.getName(), d.getCode()))
                .toList();
    }
}
