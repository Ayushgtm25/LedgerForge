package com.spendsmart.notification.repository;

import com.spendsmart.notification.entity.Notification;
import com.spendsmart.notification.entity.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);

    Optional<Notification> findByNotificationIdAndUserId(Long notificationId, Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    void deleteByNotificationIdAndUserId(Long notificationId, Long userId);
}
