package com.smartlibrary.backend.dto.auth;

public class AuthMessageResponse {
    private String message;

    public AuthMessageResponse() {}

    public AuthMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
