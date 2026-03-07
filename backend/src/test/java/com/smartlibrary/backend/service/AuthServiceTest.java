package com.smartlibrary.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartlibrary.backend.dto.auth.AuthRequest;
import com.smartlibrary.backend.dto.auth.AuthResponse;
import com.smartlibrary.backend.dto.auth.RegisterRequest;
import com.smartlibrary.backend.repository.PasswordResetTokenRepository;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.entity.enums.UserRole;
import com.smartlibrary.backend.repository.UserRepository;
import com.smartlibrary.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private SmsService smsService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void registerCreatesUserAndReturnsToken() {
        AuthService authService = new AuthService(
                userRepository, passwordResetTokenRepository, passwordEncoder, jwtService, smsService);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");
        request.setFullName("Alice");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(jwtService.generateToken("alice")).thenReturn("token-1");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User persisted = userCaptor.getValue();

        assertEquals("alice", persisted.getUsername());
        assertEquals("alice@example.com", persisted.getEmail());
        assertEquals(UserRole.USER, persisted.getRole());
        assertNotNull(persisted.getPassword());
        assertTrue(passwordEncoder.matches("secret123", persisted.getPassword()));
        assertEquals("token-1", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals(UserRole.USER, response.getRole());
    }

    @Test
    void loginRejectsInvalidPassword() {
        AuthService authService = new AuthService(
                userRepository, passwordResetTokenRepository, passwordEncoder, jwtService, smsService);

        AuthRequest request = new AuthRequest();
        request.setUsername("alice");
        request.setPassword("wrongpass");

        User existing = new User();
        existing.setId(10L);
        existing.setUsername("alice");
        existing.setPassword(passwordEncoder.encode("secret123"));
        existing.setRole(UserRole.USER);

        when(userRepository.findByUsernameOrEmail("alice", "alice")).thenReturn(java.util.Optional.of(existing));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
