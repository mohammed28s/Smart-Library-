package com.smartlibrary.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    @NotBlank(message = "username or email is required")
    @Size(max = 255, message = "username/email must be at most 255 characters")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 255, message = "password must be between 6 and 255 characters")
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
