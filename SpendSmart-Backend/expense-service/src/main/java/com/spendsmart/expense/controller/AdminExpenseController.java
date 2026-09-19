package com.spendsmart.expense.controller;

import com.spendsmart.expense.dto.ExpenseDto;
import com.spendsmart.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/expenses")
@RequiredArgsConstructor
@Tag(name = "Admin Expenses", description = "Admin-only expense endpoints")
public class AdminExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @Operation(summary = "Get all expenses (admin)")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/count")
    @Operation(summary = "Get total expense count (admin)")
    public ResponseEntity<Map<String, Long>> getExpenseCount() {
        return ResponseEntity.ok(Map.of("count", expenseService.getTotalExpenseCount()));
    }

    @GetMapping("/totals")
    @Operation(summary = "Get platform expense totals (admin)")
    public ResponseEntity<Map<String, BigDecimal>> getTotals() {
        return ResponseEntity.ok(Map.of("total", expenseService.getTotalAcrossAllUsers()));
    }
}
