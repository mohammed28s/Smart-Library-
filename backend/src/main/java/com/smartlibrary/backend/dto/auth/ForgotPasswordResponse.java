package com.smartlibrary.backend.dto.auth;

public class ForgotPasswordResponse {
    private String message;
    private String resetToken;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
