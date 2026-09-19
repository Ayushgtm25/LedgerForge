package com.spendsmart.recurring.controller;

import com.spendsmart.recurring.dto.RecurringRuleRequest;
import com.spendsmart.recurring.dto.RecurringRuleResponse;
import com.spendsmart.recurring.security.JwtUserDetails;
import com.spendsmart.recurring.service.RecurringServiceImpl;
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
@RequestMapping("/recurring")
@RequiredArgsConstructor
@Tag(name = "Recurring", description = "Recurring transaction rule management")
public class RecurringController {

    private final RecurringServiceImpl recurringService;

    @PostMapping
    @Operation(summary = "Create a new recurring rule")
    public ResponseEntity<RecurringRuleResponse> createRule(
            @AuthenticationPrincipal JwtUserDetails user,
            @Valid @RequestBody RecurringRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recurringService.createRule(user.getUserId(), request));
    }

    @GetMapping
    @Operation(summary = "Get all recurring rules for the user")
    public ResponseEntity<List<RecurringRuleResponse>> getRules(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(recurringService.getRulesByUser(user.getUserId()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active recurring rules")
    public ResponseEntity<List<RecurringRuleResponse>> getActiveRules(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(recurringService.getActiveRules(user.getUserId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recurring rule by ID")
    public ResponseEntity<RecurringRuleResponse> getRuleById(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(recurringService.getRuleById(user.getUserId(), id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a recurring rule")
    public ResponseEntity<RecurringRuleResponse> updateRule(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody RecurringRuleRequest request) {
        return ResponseEntity.ok(recurringService.updateRule(user.getUserId(), id, request));
    }

    @PutMapping("/{id}/toggle")
    @Operation(summary = "Toggle active/inactive status")
    public ResponseEntity<RecurringRuleResponse> toggleActive(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(recurringService.toggleActive(user.getUserId(), id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recurring rule")
    public ResponseEntity<Void> deleteRule(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        recurringService.deleteRule(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get active recurring rule count")
    public ResponseEntity<Map<String, Long>> getActiveCount(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(Map.of("count", recurringService.getActiveCount(user.getUserId())));
    }
}
