package com.spendsmart.income.controller;

import com.spendsmart.income.dto.IncomeRequest;
import com.spendsmart.income.dto.IncomeResponse;
import com.spendsmart.income.entity.Income.IncomeSource;
import com.spendsmart.income.security.JwtUserDetails;
import com.spendsmart.income.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
@Tag(name = "Income", description = "Income management endpoints")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    @Operation(summary = "Create a new income entry")
    public ResponseEntity<IncomeResponse> createIncome(
            @AuthenticationPrincipal JwtUserDetails user,
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incomeService.createIncome(user.getUserId(), request));
    }

    @GetMapping
    @Operation(summary = "Get all incomes for the authenticated user")
    public ResponseEntity<List<IncomeResponse>> getIncomes(
            @AuthenticationPrincipal JwtUserDetails user,
            @RequestParam(required = false) IncomeSource source,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<IncomeResponse> incomes;

        if (keyword != null && !keyword.isBlank()) {
            incomes = incomeService.searchIncomes(user.getUserId(), keyword);
        } else if (source != null) {
            incomes = incomeService.getIncomesBySource(user.getUserId(), source);
        } else if (from != null && to != null) {
            incomes = incomeService.getIncomesByDateRange(user.getUserId(), from, to);
        } else {
            incomes = incomeService.getIncomesByUser(user.getUserId());
        }

        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an income by ID")
    public ResponseEntity<IncomeResponse> getIncomeById(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(incomeService.getIncomeById(user.getUserId(), id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an income entry")
    public ResponseEntity<IncomeResponse> updateIncome(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(incomeService.updateIncome(user.getUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an income entry")
    public ResponseEntity<Void> deleteIncome(
            @AuthenticationPrincipal JwtUserDetails user,
            @PathVariable Long id) {
        incomeService.deleteIncome(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/totals")
    @Operation(summary = "Get income totals by source")
    public ResponseEntity<Map<String, BigDecimal>> getTotals(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(incomeService.getIncomeTotals(user.getUserId()));
    }
}
