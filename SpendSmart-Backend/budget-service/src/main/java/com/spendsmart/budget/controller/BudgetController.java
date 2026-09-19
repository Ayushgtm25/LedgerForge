package com.spendsmart.budget.controller;

import com.spendsmart.budget.dto.BudgetRequest;
import com.spendsmart.budget.dto.BudgetResponse;
import com.spendsmart.budget.dto.BudgetSpentUpdate;
import com.spendsmart.budget.security.JwtUserDetails;
import com.spendsmart.budget.service.BudgetService;
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
@RequestMapping("/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Budget management endpoints")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticationPrincipal JwtUserDetails user,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(user.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all budgets for the authenticated user")
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(budgetService.getBudgetsByUser(user.getUserId()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active budgets for the authenticated user")
    public ResponseEntity<List<BudgetResponse>> getActiveBudgets(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(budgetService.getActiveBudgets(user.getUserId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a budget by ID")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(user.getUserId(), id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(user.getUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        budgetService.deleteBudget(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Internal endpoint called by expense-service to update spent amounts.
     * This does not require user-level authentication (service-to-service).
     */
    @PostMapping("/spent")
    @Operation(summary = "Update budget spent amount (inter-service)")
    public ResponseEntity<Map<String, String>> updateSpentAmount(
            @Valid @RequestBody BudgetSpentUpdate update) {
        budgetService.updateSpentAmount(update);
        return ResponseEntity.ok(Map.of("message", "Spent amount updated"));
    }

    @GetMapping("/exceeded")
    @Operation(summary = "Get budgets that exceeded their threshold")
    public ResponseEntity<List<BudgetResponse>> getExceededBudgets(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(budgetService.getExceededBudgets(user.getUserId()));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total budget count for the user")
    public ResponseEntity<Map<String, Long>> getBudgetCount(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(Map.of("count", budgetService.getBudgetCount(user.getUserId())));
    }
}
