package com.spendsmart.notification.messaging;

import com.spendsmart.notification.dto.CreateNotificationRequest;
import com.spendsmart.notification.service.EmailService;
import com.spendsmart.notification.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventListener {

    private final EmailService emailService;
    private final NotificationServiceImpl notificationService;

    @RabbitListener(queues = "${app.messaging.auth-notification-queue}")
    public void handleAuthEvent(AuthNotificationEvent event) {
        log.info("Received auth event: {} for user: {}", event.getEventType(), event.getEmail());

        // Process OTP emails
        if ("OTP_CHALLENGE_CREATED".equals(event.getEventType()) && event.getOtpCode() != null) {
            emailService.sendOtpEmail(event.getEmail(), event.getTitle(), event.getOtpCode());
        }

        if (event.getUserId() == null) {
            return;
        }

        try {
            CreateNotificationRequest request = new CreateNotificationRequest();
            request.setUserId(event.getUserId());
            request.setType("SYSTEM");
            request.setTitle(event.getTitle());
            request.setMessage(event.getMessage());
            request.setSeverity(event.getSeverity());

            notificationService.createNotification(request);
        } catch (Exception e) {
            log.error("Failed to create in-app notification for auth event: {}", e.getMessage());
        }
    }
}
