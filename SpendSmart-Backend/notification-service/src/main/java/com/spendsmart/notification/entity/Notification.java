package com.spendsmart.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notif_user_id", columnList = "user_id"),
        @Index(name = "idx_notif_read", columnList = "user_id, is_read"),
        @Index(name = "idx_notif_type", columnList = "type"),
        @Index(name = "idx_notif_created", columnList = "created_at")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Severity severity = Severity.INFO;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        BUDGET_ALERT,
        RECURRING_REMINDER,
        MONTHLY_DIGEST,
        ADMIN_BROADCAST,
        SYSTEM
    }

    public enum Severity {
        INFO,
        WARNING,
        CRITICAL
    }
}
