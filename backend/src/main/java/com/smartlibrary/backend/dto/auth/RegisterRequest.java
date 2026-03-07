package com.smartlibrary.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Pattern(regexp = "^[A-Za-z]{3,30}$", message = "username must be 3-30 letters only (no numbers or special characters)")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 255, message = "password must be between 8 and 255 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "password must include at least one uppercase letter, one lowercase letter, and one number")
    private String password;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 255, message = "email must be at most 255 characters")
    private String email;

    @Pattern(regexp = "^$|^\\+?[1-9]\\d{7,14}$", message = "phone must be a valid international number")
    @Size(max = 20, message = "phone must be at most 20 characters")
    private String phone;

    @Pattern(regexp = "^$|^[A-Za-z ]{2,255}$", message = "fullName must contain letters and spaces only")
    @Size(max = 255, message = "fullName must be at most 255 characters")
    private String fullName;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
