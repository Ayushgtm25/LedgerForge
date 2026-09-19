package com.spendsmart.notification.service;

import com.spendsmart.notification.dto.BroadcastRequest;
import com.spendsmart.notification.dto.CreateNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBroadcastService {

    private final RestTemplate loadBalancedRestTemplate;
    private final NotificationServiceImpl notificationService;
    private final EmailService emailService;

    public int broadcast(String authorization, BroadcastRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = loadBalancedRestTemplate.exchange(
                "http://AUTH-SERVICE/admin/users?active=true",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        List<Map<String, Object>> users = response.getBody();
        if (users == null || users.isEmpty()) {
            return 0;
        }

        String severity = request.getSeverity() != null ? request.getSeverity() : "INFO";
        int delivered = 0;

        for (Map<String, Object> userRow : users) {
            try {
                if (userRow.get("userId") == null) {
                    continue;
                }
                long userId = ((Number) userRow.get("userId")).longValue();
                String role = userRow.get("role") != null ? String.valueOf(userRow.get("role")) : "";
                if ("ADMIN".equalsIgnoreCase(role)) {
                    continue;
                }

                String subscription = userRow.get("subscriptionType") != null
                        ? String.valueOf(userRow.get("subscriptionType"))
                        : "NORMAL";
                String email = userRow.get("email") != null ? String.valueOf(userRow.get("email")) : null;

                CreateNotificationRequest notification = new CreateNotificationRequest();
                notification.setUserId(userId);
                notification.setType("ADMIN_BROADCAST");
                notification.setTitle(request.getTitle());
                notification.setMessage(request.getMessage());
                notification.setSeverity(severity);
                notificationService.createNotification(notification);

                if ("PAID".equalsIgnoreCase(subscription) && email != null && !email.isBlank()) {
                    emailService.sendBroadcastEmail(email, request.getTitle(), request.getMessage());
                }
                delivered++;
            } catch (Exception ex) {
                log.warn("Broadcast failed for row {}: {}", userRow, ex.getMessage());
            }
        }

        return delivered;
    }
}
