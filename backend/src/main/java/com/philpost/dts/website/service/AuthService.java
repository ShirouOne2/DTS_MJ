package com.philpost.dts.website.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.dto.UserInfoDto;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.exception.UnauthorizedException;
import com.philpost.dts.website.repository.UserRepository;

// service/AuthService.java
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserInfoDto login(String username, String password) {
        User user = userRepository.findByUsername(username);
        // One message for unknown user, wrong password and inactive account,
        // so the response never reveals which usernames exist.
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())
                || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        user.setLastLoginAt(LocalDateTime.now());
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
}