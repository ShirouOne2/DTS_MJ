package com.philpost.dts.website.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.dto.CreateUserRequest;
import com.philpost.dts.website.dto.UpdateUserRequest;
import com.philpost.dts.website.dto.UserAdminDto;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.Department;
import com.philpost.dts.website.entity.Role;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;
import com.philpost.dts.website.repository.DepartmentRepository;
import com.philpost.dts.website.repository.UserRepository;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserAdminService(UserRepository userRepository, DepartmentRepository departmentRepository,
                            PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<UserAdminDto> list() {
        return userRepository.findAllWithDepartment().stream().map(this::toDto).toList();
    }

    @Transactional
    public UserAdminDto create(CreateUserRequest request) {
        String username = request.username().trim().toLowerCase();
        if (userRepository.existsByUsername(username)) {
            throw new BusinessRuleException("Username already in use: " + username);
        }
        User user = userRepository.save(User.builder()
                .department(activeDepartment(request.departmentId()))
                .username(username)
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .email(blankToNull(request.email()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build());

        auditService.log(AuditAction.USER_CREATED, "USER", user.getUserId(),
        "username=%s, role=%s, department=%s".formatted(user.getUsername(), user.getRole(), user.getDepartment().getName()));
        return toDto(user);
        
    }

    @Transactional
    public UserAdminDto update(Long id, UpdateUserRequest request, String actorUsername) {
        User user = find(id);

        if (user.getUsername().equalsIgnoreCase(actorUsername)
                && request.role() != Role.ADMIN) {
            throw new BusinessRuleException("You can't remove your own admin role.");
        }

        // Capture existing values BEFORE making changes
        Role oldRole = user.getRole();
        String oldDepartment = user.getDepartment().getName();

        String oldProfile = user.getFirstName() + "|"
                + user.getLastName() + "|"
                + user.getEmail();

        // Apply requested changes
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(blankToNull(request.email()));
        user.setRole(request.role());

        if (!user.getDepartment().getDepartmentId()
                .equals(request.departmentId())) {
            user.setDepartment(activeDepartment(request.departmentId()));
        }

        // Audit role changes
        if (oldRole != user.getRole()) {
            auditService.log(
                    AuditAction.ROLE_CHANGED,
                    "USER",
                    id,
                    user.getUsername() + ": " + oldRole
                            + " -> " + user.getRole()
            );
        }

        // Audit department changes
        String newDepartment = user.getDepartment().getName();

        if (!oldDepartment.equals(newDepartment)) {
            auditService.log(
                    AuditAction.USER_UPDATED,
                    "USER",
                    id,
                    user.getUsername() + ": department "
                            + oldDepartment + " -> " + newDepartment
            );
        }

        // Audit profile changes
        String newProfile = user.getFirstName() + "|"
                + user.getLastName() + "|"
                + user.getEmail();

        if (!oldProfile.equals(newProfile)) {
            auditService.log(
                    AuditAction.USER_UPDATED,
                    "USER",
                    id,
                    user.getUsername() + ": profile information changed"
            );
        }

        return toDto(user);
    }

    @Transactional
    public UserAdminDto setActive(Long id, boolean active, String actorUsername) {
        User user = find(id);
        if (!active && user.getUsername().equals(actorUsername)) {
            throw new BusinessRuleException("You can't deactivate your own account.");
        }
        boolean wasActive = Boolean.TRUE.equals(user.getIsActive());
        user.setIsActive(active);
        if (wasActive != active) {
            auditService.log(active ? AuditAction.USER_ACTIVATED : AuditAction.USER_DEACTIVATED,
                    "USER", id, user.getUsername());
        }
        return toDto(user);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = find(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        auditService.log(AuditAction.PASSWORD_RESET, "USER", id, user.getUsername());
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private Department activeDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found: " + id));
        if (!Boolean.TRUE.equals(department.getIsActive())) {
            throw new BusinessRuleException("That department is inactive.");
        }
        return department;
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private UserAdminDto toDto(User u) {
        return new UserAdminDto(u.getUserId(), u.getUsername(), u.getFirstName(), u.getLastName(),
                u.getEmail(), u.getRole().name(), u.getDepartment().getDepartmentId(),
                u.getDepartment().getName(), Boolean.TRUE.equals(u.getIsActive()), u.getLastLoginAt());
    }
}