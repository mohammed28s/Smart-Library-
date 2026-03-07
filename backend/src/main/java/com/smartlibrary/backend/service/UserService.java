package com.smartlibrary.backend.service;

import com.smartlibrary.backend.dto.UserDto;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto findById(Long id) {
        return toDto(getUser(id));
    }

    public UserDto create(UserDto dto) {
        User user = new User();
        apply(user, dto);
        return toDto(userRepository.save(user));
    }

    public UserDto update(Long id, UserDto dto) {
        User user = getUser(id);
        apply(user, dto);
        return toDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private void apply(User user, UserDto dto) {
        user.setUsername(dto.getUsername());
        user.setEmail(normalizeEmail(dto.getEmail()));
        user.setPhone(normalizePhone(dto.getPhone()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        return dto;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        return phone == null || phone.isBlank() ? null : phone.trim();
    }
}
