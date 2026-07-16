package com.tabseer.collabdocs.controller;

import com.tabseer.collabdocs.dto.request.LoginRequest;
import com.tabseer.collabdocs.dto.request.RegisterRequest;
import com.tabseer.collabdocs.dto.response.AuthResponse;
import com.tabseer.collabdocs.dto.response.UserResponse;
import com.tabseer.collabdocs.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.tabseer.collabdocs.dto.response.MeResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }
}