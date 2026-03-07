package com.smartlibrary.backend.controller;

import com.smartlibrary.backend.dto.assistant.AssistantChatRequest;
import com.smartlibrary.backend.dto.assistant.AssistantChatResponse;
import com.smartlibrary.backend.dto.assistant.AssistantFeedbackRequest;
import com.smartlibrary.backend.dto.assistant.AssistantFeedbackResponse;
import com.smartlibrary.backend.service.AssistantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
@Tag(name = "Assistant", description = "AI assistant endpoints")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public AssistantChatResponse chat(
            @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody AssistantChatRequest request) {
        String username = userDetails == null ? null : userDetails.getUsername();
        return assistantService.chat(username, request);
    }

    @PostMapping("/feedback")
    public AssistantFeedbackResponse feedback(
            @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody AssistantFeedbackRequest request) {
        String username = userDetails == null ? null : userDetails.getUsername();
        return assistantService.saveFeedback(username, request);
    }
}
