package com.smartlibrary.backend.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final String mode;
    private final String accountSid;
    private final String authToken;
    private final String fromNumber;

    public SmsService(
            @Value("${app.sms.mode:mock}") String mode,
            @Value("${app.sms.twilio.account-sid:}") String accountSid,
            @Value("${app.sms.twilio.auth-token:}") String authToken,
            @Value("${app.sms.twilio.from-number:}") String fromNumber) {
        this.mode = mode;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
    }

    public boolean sendResetCode(String phone, String code) {
        String message = "Your SmartLibrary password reset code is: " + code;
        if ("twilio".equalsIgnoreCase(mode)) {
            sendViaTwilio(phone, message);
            return false;
        }
        System.out.println("MOCK SMS to " + phone + " -> " + message);
        return true;
    }

    private void sendViaTwilio(String to, String bodyText) {
        if (isBlank(accountSid) || isBlank(authToken) || isBlank(fromNumber)) {
            throw new IllegalStateException("Twilio SMS is not configured");
        }

        String body = "To=" + encode(to)
                + "&From=" + encode(fromNumber)
                + "&Body=" + encode(bodyText);

        String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("Twilio SMS failed: " + response.body());
            }
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to send SMS", ex);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
