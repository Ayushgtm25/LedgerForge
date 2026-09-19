package com.spendsmart.notification.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Long userId;
    private String type;
    private String title;
    private String message;
    private String severity;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
