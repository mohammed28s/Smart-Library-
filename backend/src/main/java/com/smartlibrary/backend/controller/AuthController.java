package com.smartlibrary.backend.controller;

import com.smartlibrary.backend.dto.auth.AuthRequest;
import com.smartlibrary.backend.dto.auth.AuthMessageResponse;
import com.smartlibrary.backend.dto.auth.AuthResponse;
import com.smartlibrary.backend.dto.auth.ForgotPasswordRequest;
import com.smartlibrary.backend.dto.auth.ForgotPasswordResponse;
import com.smartlibrary.backend.dto.auth.ForgotPasswordSmsRequest;
import com.smartlibrary.backend.dto.auth.ForgotPasswordSmsResponse;
import com.smartlibrary.backend.dto.auth.RegisterRequest;
import com.smartlibrary.backend.dto.auth.ResetPasswordRequest;
import com.smartlibrary.backend.dto.auth.ResetPasswordSmsRequest;
import com.smartlibrary.backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/forgot-password-sms")
    public ForgotPasswordSmsResponse forgotPasswordSms(@Valid @RequestBody ForgotPasswordSmsRequest request) {
        return authService.forgotPasswordSms(request);
    }

    @PostMapping("/reset-password")
    public AuthMessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    @PostMapping("/reset-password-sms")
    public AuthMessageResponse resetPasswordSms(@Valid @RequestBody ResetPasswordSmsRequest request) {
        return authService.resetPasswordSms(request);
    }
}
