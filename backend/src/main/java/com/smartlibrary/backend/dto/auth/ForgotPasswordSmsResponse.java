package com.smartlibrary.backend.dto.auth;

public class ForgotPasswordSmsResponse {
    private String message;
    private String resetCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
}
