package com.smartlibrary.backend.dto.assistant;

public class AssistantFeedbackResponse {

    private String message;

    public AssistantFeedbackResponse() {}

    public AssistantFeedbackResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
