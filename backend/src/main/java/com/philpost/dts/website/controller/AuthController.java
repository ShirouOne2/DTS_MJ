package com.philpost.dts.website.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philpost.dts.website.dto.LoginRequest;
import com.philpost.dts.website.dto.UserInfoDto;
import com.philpost.dts.website.security.JwtUtil;
import com.philpost.dts.website.service.AuthService;

import jakarta.validation.Valid;

// controller/AuthController.java
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfoDto> login(@Valid @RequestBody LoginRequest request) {
        UserInfoDto user = authService.login(request.username(), request.password());
        String token = jwtUtil.generateToken(user.username(), user.role());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie(token, jwtUtil.getExpirationSeconds()).toString())
                .body(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        // authentication is null if the cookie was already expired; nothing to record then.
        if (authentication != null) {
            authService.recordLogout(authentication.getName());
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie("", 0).toString())
                .build();
    }

    @GetMapping("/me")
    public UserInfoDto me(Authentication authentication) {
        return authService.me(authentication.getName());
    }

    private ResponseCookie cookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from("jwt", value)
                .httpOnly(true)
                .secure(cookieSecure)   // set app.cookie.secure=true in production (HTTPS)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }
}