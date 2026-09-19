package com.spendsmart.income.controller;

import com.spendsmart.income.dto.IncomeResponse;
import com.spendsmart.income.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/incomes")
@RequiredArgsConstructor
@Tag(name = "Admin Income", description = "Admin-only income management")
public class AdminIncomeController {

    private final IncomeService incomeService;

    @GetMapping
    @Operation(summary = "Get all incomes (admin)")
    public ResponseEntity<List<IncomeResponse>> getAllIncomes() {
        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    @GetMapping("/count")
    @Operation(summary = "Get total income count (admin)")
    public ResponseEntity<Map<String, Long>> getTotalCount() {
        return ResponseEntity.ok(Map.of("count", incomeService.getTotalIncomeCount()));
    }
}
