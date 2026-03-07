package com.smartlibrary.backend.service;

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
import com.smartlibrary.backend.entity.PasswordResetToken;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.entity.enums.UserRole;
import com.smartlibrary.backend.repository.PasswordResetTokenRepository;
import com.smartlibrary.backend.repository.UserRepository;
import com.smartlibrary.backend.security.JwtService;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SmsService smsService;

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            SmsService smsService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.smsService = smsService;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        String phone = normalizePhone(request.getPhone());
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(UserRole.USER);
        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(AuthRequest request) {
        String identifier = request.getUsername().trim();
        User user = userRepository.findByUsernameOrEmail(identifier, normalizeEmail(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return buildAuthResponse(user);
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        String message = "If this email exists, a reset token has been generated.";
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setMessage(message);

        userRepository.findByEmail(email).ifPresent(user -> {
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setCreatedAt(LocalDateTime.now());
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            passwordResetTokenRepository.save(resetToken);
            // Demo mode: return reset token directly instead of email delivery.
            response.setResetToken(resetToken.getToken());
        });

        return response;
    }

    public AuthMessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenAndUsedAtIsNull(request.getToken().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Invalid or expired reset token");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);

        return new AuthMessageResponse("Password has been reset successfully");
    }

    public ForgotPasswordSmsResponse forgotPasswordSms(ForgotPasswordSmsRequest request) {
        String phone = normalizePhone(request.getPhone());
        String message = "If this phone exists, a reset code has been sent.";
        ForgotPasswordSmsResponse response = new ForgotPasswordSmsResponse();
        response.setMessage(message);

        userRepository.findByPhone(phone).ifPresent(user -> {
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(code);
            resetToken.setCreatedAt(LocalDateTime.now());
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            passwordResetTokenRepository.save(resetToken);
            boolean mockMode = smsService.sendResetCode(phone, code);
            if (mockMode) {
                response.setResetCode(code);
            }
        });

        return response;
    }

    public AuthMessageResponse resetPasswordSms(ResetPasswordSmsRequest request) {
        String phone = normalizePhone(request.getPhone());
        PasswordResetToken token = passwordResetTokenRepository
                .findFirstByUserPhoneAndTokenAndUsedAtIsNullOrderByCreatedAtDesc(phone, request.getCode().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset code"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Invalid or expired reset code");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);

        return new AuthMessageResponse("Password has been reset successfully");
    }

    private AuthResponse buildAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        response.setToken(jwtService.generateToken(user.getUsername()));
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        return response;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        return phone == null || phone.isBlank() ? null : phone.trim();
    }
}
