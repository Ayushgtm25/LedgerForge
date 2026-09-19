package com.spendsmart.notification.controller;

import com.spendsmart.notification.dto.CreateNotificationRequest;
import com.spendsmart.notification.dto.NotificationResponse;
import com.spendsmart.notification.security.JwtUserDetails;
import com.spendsmart.notification.service.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications for the authenticated user")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(notificationService.getNotifications(user.getUserId()));
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getUserId()));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getUserId())));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(user.getUserId(), id));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @AuthenticationPrincipal JwtUserDetails user) {
        notificationService.markAllAsRead(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        notificationService.deleteNotification(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    /** Internal endpoint for other services to create notifications. */
    @PostMapping("/internal")
    @Operation(summary = "Create a notification (inter-service)")
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(request));
    }
}
