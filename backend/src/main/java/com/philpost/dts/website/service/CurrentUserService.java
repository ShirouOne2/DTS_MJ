package com.philpost.dts.website.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.exception.UnauthorizedException;
import com.philpost.dts.website.repository.UserRepository;

// service/CurrentUserService.java
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Not signed in");
        }
        User user = userRepository.findByUsername(auth.getName());
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Account not found or inactive");
        }
        return user;
    }
}