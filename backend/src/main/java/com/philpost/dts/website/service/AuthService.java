package com.philpost.dts.website.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.dto.UserInfoDto;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.exception.UnauthorizedException;
import com.philpost.dts.website.repository.UserRepository;

// service/AuthService.java
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional
    public UserInfoDto login(String username, String password) {
        User user = userRepository.findByUsername(username);

        String failure = null;
        if (user == null) failure = "unknown username";
        else if (!Boolean.TRUE.equals(user.getIsActive())) failure = "account inactive";
        else if (!passwordEncoder.matches(password, user.getPasswordHash())) failure = "wrong password";

        if (failure != null) {
            // Own transaction, so this row survives the exception below.
            auditService.logIndependent(username, AuditAction.LOGIN_FAILED, "USER",
                    user != null ? user.getUserId() : null, failure);
            // Same message for every cause, so the response never reveals which usernames exist.
            throw new UnauthorizedException("Invalid username or password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        auditService.logAs(user.getUsername(), AuditAction.LOGIN, "USER", user.getUserId(), null);
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public UserInfoDto me(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Account not found or inactive");
        }
        return toDto(user);
    }

    private UserInfoDto toDto(User u) {
        return new UserInfoDto(u.getUserId(), u.getUsername(),
                u.getFirstName() + " " + u.getLastName(), u.getRole().name(),
                u.getDepartment().getDepartmentId(), u.getDepartment().getName());
    }
    @Transactional
    public void recordLogout(String username) {
        User user = userRepository.findByUsername(username);
        auditService.logAs(username, AuditAction.LOGOUT, "USER", user != null ? user.getUserId() : null, null);
    }
}