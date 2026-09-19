package com.spendsmart.notification.service;

import com.spendsmart.notification.dto.CreateNotificationRequest;
import com.spendsmart.notification.dto.NotificationResponse;
import com.spendsmart.notification.entity.Notification;
import com.spendsmart.notification.entity.Notification.NotificationType;
import com.spendsmart.notification.entity.Notification.Severity;
import com.spendsmart.notification.exception.ResourceNotFoundException;
import com.spendsmart.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl {

    private final NotificationRepository notificationRepository;

    public NotificationResponse createNotification(CreateNotificationRequest request) {
        log.info("Creating notification for user {}: {}", request.getUserId(), request.getTitle());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type(NotificationType.valueOf(request.getType()))
                .title(request.getTitle())
                .message(request.getMessage())
                .severity(request.getSeverity() != null ? Severity.valueOf(request.getSeverity()) : Severity.INFO)
                .build();

        notification = notificationRepository.save(notification);
        log.info("Notification created: id={}", notification.getNotificationId());
        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        log.info("Marking notification {} as read for user {}", notificationId, userId);
        Notification notification = findByIdAndUser(notificationId, userId);
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user {}", userId);
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification n : unread) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
        log.info("Marked {} notifications as read", unread.size());
    }

    public void deleteNotification(Long userId, Long notificationId) {
        log.info("Deleting notification {} for user {}", notificationId, userId);
        Notification notification = findByIdAndUser(notificationId, userId);
        notificationRepository.delete(notification);
    }

    private Notification findByIdAndUser(Long notificationId, Long userId) {
        return notificationRepository.findByNotificationIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Notification %d not found for user %d", notificationId, userId)));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .userId(n.getUserId())
                .type(n.getType().name())
                .title(n.getTitle())
                .message(n.getMessage())
                .severity(n.getSeverity().name())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
