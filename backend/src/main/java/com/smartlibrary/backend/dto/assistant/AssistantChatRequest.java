package com.smartlibrary.backend.dto.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AssistantChatRequest {

    @NotBlank(message = "Question is required")
    @Size(max = 1000, message = "Question must be at most 1000 characters")
    private String question;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
