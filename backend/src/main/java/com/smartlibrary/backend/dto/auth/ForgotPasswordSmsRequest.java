package com.smartlibrary.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ForgotPasswordSmsRequest {

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "phone must be a valid international number")
    @Size(max = 20, message = "phone must be at most 20 characters")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
