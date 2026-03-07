package com.smartlibrary.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlibrary.backend.dto.assistant.AssistantChatRequest;
import com.smartlibrary.backend.dto.assistant.AssistantChatResponse;
import com.smartlibrary.backend.dto.assistant.AssistantFeedbackRequest;
import com.smartlibrary.backend.dto.assistant.AssistantFeedbackResponse;
import com.smartlibrary.backend.entity.AssistantFeedback;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.repository.AssistantFeedbackRepository;
import com.smartlibrary.backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private AssistantFeedbackRepository assistantFeedbackRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void chatFallsBackWhenNoApiKey() {
        AssistantService assistantService = new AssistantService(
                assistantFeedbackRepository,
                userRepository,
                new ObjectMapper(),
                "https://api.deepseek.com",
                "",
                "deepseek-chat");

        AssistantChatRequest request = new AssistantChatRequest();
        request.setQuestion("How can I rent a book?");

        AssistantChatResponse response = assistantService.chat("alice", request);

        assertEquals("local-fallback", response.getProvider());
        assertNotNull(response.getAnswer());
    }

    @Test
    void saveFeedbackLinksKnownUser() {
        AssistantService assistantService = new AssistantService(
                assistantFeedbackRepository,
                userRepository,
                new ObjectMapper(),
                "https://api.deepseek.com",
                "",
                "deepseek-chat");

        User user = new User();
        user.setId(5L);
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(assistantFeedbackRepository.save(any(AssistantFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AssistantFeedbackRequest request = new AssistantFeedbackRequest();
        request.setQuestion("How can I reset my password?");
        request.setAnswer("Use forgot password.");
        request.setHelpful(true);
        request.setComment("Worked.");

        AssistantFeedbackResponse response = assistantService.saveFeedback("alice", request);

        ArgumentCaptor<AssistantFeedback> feedbackCaptor = ArgumentCaptor.forClass(AssistantFeedback.class);
        verify(assistantFeedbackRepository).save(feedbackCaptor.capture());
        AssistantFeedback saved = feedbackCaptor.getValue();
        assertEquals("alice", saved.getUser().getUsername());
        assertEquals("How can I reset my password?", saved.getQuestion());
        assertEquals("Use forgot password.", saved.getAnswer());
        assertEquals("Thanks for your feedback. We use it to improve answers.", response.getMessage());
    }
}
