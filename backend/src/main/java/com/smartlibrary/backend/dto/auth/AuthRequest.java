package com.smartlibrary.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    @NotBlank(message = "username or email is required")
    @Size(max = 255, message = "username/email must be at most 255 characters")
    @Pattern(
            regexp = "^(?:[A-Za-z]{3,30}|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})$",
            message = "username must be 3-30 letters only, or enter a valid email address")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 255, message = "password must be between 8 and 255 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
