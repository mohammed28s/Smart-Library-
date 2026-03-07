package com.smartlibrary.backend.dto.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AssistantFeedbackRequest {

    @NotBlank(message = "Question is required")
    @Size(max = 1000, message = "Question must be at most 1000 characters")
    private String question;

    @NotBlank(message = "Answer is required")
    @Size(max = 6000, message = "Answer must be at most 6000 characters")
    private String answer;

    @NotNull(message = "Helpful flag is required")
    private Boolean helpful;

    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String comment;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean getHelpful() {
        return helpful;
    }

    public void setHelpful(Boolean helpful) {
        this.helpful = helpful;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
