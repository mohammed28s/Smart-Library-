package com.smartlibrary.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlibrary.backend.dto.assistant.AssistantChatRequest;
import com.smartlibrary.backend.dto.assistant.AssistantChatResponse;
import com.smartlibrary.backend.dto.assistant.AssistantFeedbackRequest;
import com.smartlibrary.backend.dto.assistant.AssistantFeedbackResponse;
import com.smartlibrary.backend.entity.AssistantFeedback;
import com.smartlibrary.backend.entity.User;
import com.smartlibrary.backend.repository.AssistantFeedbackRepository;
import com.smartlibrary.backend.repository.UserRepository;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssistantService.class);

    private final AssistantFeedbackRepository assistantFeedbackRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final String deepSeekBaseUrl;
    private final String deepSeekApiKey;
    private final String deepSeekModel;

    public AssistantService(
            AssistantFeedbackRepository assistantFeedbackRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            @Value("${app.ai.deepseek.base-url:https://api.deepseek.com}") String deepSeekBaseUrl,
            @Value("${app.ai.deepseek.api-key:}") String deepSeekApiKey,
            @Value("${app.ai.deepseek.model:deepseek-chat}") String deepSeekModel) {
        this.assistantFeedbackRepository = assistantFeedbackRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.deepSeekBaseUrl = deepSeekBaseUrl;
        this.deepSeekApiKey = deepSeekApiKey;
        this.deepSeekModel = deepSeekModel;
    }

    public AssistantChatResponse chat(String username, AssistantChatRequest request) {
        String question = request.getQuestion().trim();
        ChatResult deepSeekResult = askDeepSeek(question);
        if (deepSeekResult != null) {
            return new AssistantChatResponse(deepSeekResult.answer(), deepSeekResult.provider());
        }

        String answer = buildFallbackAnswer(question, username);
        return new AssistantChatResponse(answer, "local-fallback");
    }

    public AssistantFeedbackResponse saveFeedback(String username, AssistantFeedbackRequest request) {
        AssistantFeedback feedback = new AssistantFeedback();
        feedback.setQuestion(request.getQuestion().trim());
        feedback.setAnswer(request.getAnswer().trim());
        feedback.setHelpful(Boolean.TRUE.equals(request.getHelpful()));
        feedback.setComment(normalizeComment(request.getComment()));
        feedback.setCreatedAt(LocalDateTime.now());

        if (username != null && !username.isBlank()) {
            Optional<User> user = userRepository.findByUsername(username);
            user.ifPresent(feedback::setUser);
        }

        assistantFeedbackRepository.save(feedback);
        return new AssistantFeedbackResponse("Thanks for your feedback. We use it to improve answers.");
    }

    private ChatResult askDeepSeek(String question) {
        if (deepSeekApiKey == null || deepSeekApiKey.isBlank()) {
            return null;
        }

        String endpoint = deepSeekBaseUrl.endsWith("/")
                ? deepSeekBaseUrl + "chat/completions"
                : deepSeekBaseUrl + "/chat/completions";

        Map<String, Object> payload = Map.of(
                "model", deepSeekModel,
                "temperature", 0.3,
                "max_tokens", 400,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content",
                                "You are SmartLibrary Assistant. Help with books, orders, payments, account access, and short user guidance."),
                        Map.of("role", "user", "content", question)));

        try {
            String body = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                    .header("Authorization", "Bearer " + deepSeekApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                LOGGER.warn("DeepSeek request failed with status {}: {}", response.statusCode(), response.body());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return null;
            }

            String answer = choices.get(0).path("message").path("content").asText("").trim();
            if (answer.isEmpty()) {
                return null;
            }
            return new ChatResult(answer, "deepseek");
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.warn("DeepSeek request failed, using local fallback: {}", ex.getMessage());
            return null;
        }
    }

    private String buildFallbackAnswer(String question, String username) {
        String normalized = question.toLowerCase();
        String greetingPrefix = (username == null || username.isBlank()) ? "" : username + ", ";

        if (normalized.contains("reset") && normalized.contains("password")) {
            return greetingPrefix
                    + "use Forgot Password from login. You can reset by email token or SMS code if your phone is registered.";
        }
        if (normalized.contains("book")) {
            return greetingPrefix
                    + "open the Books page to search titles, add stock, and view barcode labels for each book.";
        }
        if (normalized.contains("order") || normalized.contains("rent") || normalized.contains("buy")) {
            return greetingPrefix
                    + "use Orders to create buy/rent records, then use Order Items and Payments to complete checkout.";
        }
        if (normalized.contains("worker") || normalized.contains("admin")) {
            return greetingPrefix
                    + "worker accounts can access the Users page. Normal users can log in and browse books and orders.";
        }
        return greetingPrefix
                + "I can help with books, orders, payments, login issues, and reset password flow. Ask a specific library question for a faster answer.";
    }

    private String normalizeComment(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private record ChatResult(String answer, String provider) {}
}
