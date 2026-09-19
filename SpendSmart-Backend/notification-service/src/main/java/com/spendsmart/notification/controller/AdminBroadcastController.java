package com.spendsmart.notification.controller;

import com.spendsmart.notification.dto.BroadcastRequest;
import com.spendsmart.notification.service.AdminBroadcastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Admin Notifications", description = "Admin broadcast endpoints")
public class AdminBroadcastController {

    private final AdminBroadcastService adminBroadcastService;

    @PostMapping("/broadcast")
    @Operation(summary = "Broadcast to all non-admin users (in-app); PAID users also receive email")
    public ResponseEntity<Map<String, Integer>> broadcast(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody BroadcastRequest body) {
        int recipients = adminBroadcastService.broadcast(authorization, body);
        return ResponseEntity.ok(Map.of("recipients", recipients));
    }
}
