package com.philpost.dts.website.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.dto.DepartmentDto;
import com.philpost.dts.website.dto.DepartmentRequest;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.Department;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;
import com.philpost.dts.website.repository.DepartmentRepository;
import com.philpost.dts.website.repository.UserRepository;

// service/DepartmentService.java
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository, AuditService auditService) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> listAll() {
        return departmentRepository.findAllByOrderByNameAsc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> listActive() {
        return departmentRepository.findByIsActiveTrueOrderByNameAsc().stream().map(this::toDto).toList();
    }

    @Transactional
    public DepartmentDto create(DepartmentRequest request) {
        String code = request.code().trim().toUpperCase();
        if (departmentRepository.existsByCode(code)) {
            throw new BusinessRuleException("Department code already in use: " + code);
        }
        Department saved = departmentRepository.save(
                Department.builder().name(request.name().trim()).code(code).build());
        auditService.log(AuditAction.DEPARTMENT_CREATED, "DEPARTMENT", saved.getDepartmentId(),
        saved.getCode() + " - " + saved.getName());
        return toDto(saved);
        
    }

    @Transactional
    public DepartmentDto update(Long id, DepartmentRequest request) {
        Department department = find(id);
        String code = request.code().trim().toUpperCase();
        departmentRepository.findByCode(code)
                .filter(other -> !other.getDepartmentId().equals(id))
                .ifPresent(other -> { throw new BusinessRuleException("Department code already in use: " + code); });
        String before = department.getCode() + " - " + department.getName();
        department.setName(request.name().trim());
        department.setCode(code);
        auditService.log(AuditAction.DEPARTMENT_UPDATED, "DEPARTMENT", id,
        before + " -> " + department.getCode() + " - " + department.getName());
        return toDto(department);
    }

    @Transactional
    public DepartmentDto setActive(Long id, boolean active) {
        Department department = find(id);
        if (!active && userRepository.existsByDepartment_DepartmentIdAndIsActiveTrue(id)) {
            throw new BusinessRuleException("This department still has active users. Move or deactivate them first.");
        }
        department.setIsActive(active);
        auditService.log(active ? AuditAction.DEPARTMENT_ACTIVATED : AuditAction.DEPARTMENT_DEACTIVATED,
        "DEPARTMENT", id, department.getCode());
        return toDto(department);
    }

    private Department find(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found: " + id));
    }

    private DepartmentDto toDto(Department d) {
        return new DepartmentDto(d.getDepartmentId(), d.getName(), d.getCode(), Boolean.TRUE.equals(d.getIsActive()));
    }
}