package com.smartlibrary.backend.dto.assistant;

public class AssistantChatResponse {

    private String answer;
    private String provider;

    public AssistantChatResponse() {}

    public AssistantChatResponse(String answer, String provider) {
        this.answer = answer;
        this.provider = provider;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
