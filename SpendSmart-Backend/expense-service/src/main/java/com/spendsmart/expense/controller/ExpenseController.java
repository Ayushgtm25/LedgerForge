/**
 * ExpenseController provides REST API endpoints for expense management.
 *
 * All endpoints enforce JWT authentication and return consistent JSON responses.
 * User context (userId) is extracted from the JWT token, never from the request body.
 *
 * Endpoints support multi-criteria filtering via query parameters.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.controller;

import com.spendsmart.expense.dto.ExpenseDto;
import com.spendsmart.expense.dto.UpdateExpenseDto;
import com.spendsmart.expense.security.JwtUserDetails;
import com.spendsmart.expense.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
@Slf4j
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    /**
     * Add a new expense.
     * POST /expenses
     *
     * @param authentication the authenticated user context (Spring Security)
     * @param expenseDto the expense details
     * @return the created expense with 201 status
     */
    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(
            Authentication authentication,
            @Valid @RequestBody ExpenseDto expenseDto) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: POST /expenses - User: {} - Title: {}", userId, expenseDto.getTitle());

        ExpenseDto createdExpense = expenseService.addExpense(userId, expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    /**
     * Get all expenses for the authenticated user.
     * Supports optional query parameters for filtering.
     * GET /expenses?categoryId=1&startDate=2026-01-01&endDate=2026-12-31&month=4&year=2026&type=EXPENSE&paymentMethod=CARD&keyword=groceries
     *
     * @param authentication the authenticated user context
     * @param categoryId optional category filter
     * @param startDate optional start date for range filter (yyyy-MM-dd)
     * @param endDate optional end date for range filter (yyyy-MM-dd)
     * @param month optional month filter (1-12)
     * @param year optional year filter
     * @param type optional expense type filter (EXPENSE or SPLIT)
     * @param paymentMethod optional payment method filter
     * @param keyword optional search keyword in title/notes
     * @return list of matching expenses
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(
            Authentication authentication,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String keyword) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: GET /expenses - User: {} - Filters: category={}, month={}, keyword={}",
                userId, categoryId, month, keyword);

        List<ExpenseDto> expenses;

        // Apply filters based on query parameters
        if (keyword != null && !keyword.isEmpty()) {
            expenses = expenseService.searchExpenses(userId, keyword);
        } else if (month != null && year != null) {
            expenses = expenseService.getExpensesByMonth(userId, month, year);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        } else if (type != null && !type.isEmpty()) {
            expenses = expenseService.getExpensesByType(userId, type);
        } else if (paymentMethod != null && !paymentMethod.isEmpty()) {
            expenses = expenseService.getExpensesByPaymentMethod(userId, paymentMethod);
        } else if (categoryId != null) {
            expenses = expenseService.getExpensesByCategory(userId, categoryId);
        } else {
            expenses = expenseService.getExpensesByUser(userId);
        }

        return ResponseEntity.ok(expenses);
    }

    /**
     * Get a specific expense by ID.
     * GET /expenses/{id}
     *
     * @param authentication the authenticated user context
     * @param expenseId the expense ID
     * @return the expense
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseDto> getExpenseById(
            Authentication authentication,
            @PathVariable Long expenseId) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: GET /expenses/{} - User: {}", expenseId, userId);

        ExpenseDto expense = expenseService.getExpenseById(userId, expenseId);
        return ResponseEntity.ok(expense);
    }

    /**
     * Update an expense.
     * PUT /expenses/{id}
     *
     * @param authentication the authenticated user context
     * @param expenseId the expense ID
     * @param updateDto the fields to update (all optional)
     * @return the updated expense
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseDto> updateExpense(
            Authentication authentication,
            @PathVariable Long expenseId,
            @Valid @RequestBody UpdateExpenseDto updateDto) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: PUT /expenses/{} - User: {}", expenseId, userId);

        ExpenseDto updatedExpense = expenseService.updateExpense(userId, expenseId, updateDto);
        return ResponseEntity.ok(updatedExpense);
    }

    /**
     * Delete an expense.
     * DELETE /expenses/{id}
     *
     * @param authentication the authenticated user context
     * @param expenseId the expense ID
     * @return 204 No Content on success
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            Authentication authentication,
            @PathVariable Long expenseId) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: DELETE /expenses/{} - User: {}", expenseId, userId);

        expenseService.deleteExpense(userId, expenseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get expense totals for the authenticated user.
     * Supports optional filters.
     * GET /expenses/totals?categoryId=1
     *
     * @param authentication the authenticated user context
     * @param categoryId optional category filter
     * @return response with total amount
     */
    @GetMapping("/totals")
    public ResponseEntity<Map<String, BigDecimal>> getTotals(
            Authentication authentication,
            @RequestParam(required = false) Long categoryId) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: GET /expenses/totals - User: {} - CategoryId: {}", userId, categoryId);

        BigDecimal total;
        if (categoryId != null) {
            total = expenseService.getTotalByCategory(userId, categoryId);
        } else {
            total = expenseService.getTotalByUser(userId);
        }

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("total", total);

        return ResponseEntity.ok(response);
    }

    /**
     * Upload a receipt for an expense.
     * POST /expenses/{id}/receipt
     *
     * @param authentication the authenticated user context
     * @param expenseId the expense ID
     * @param file the receipt file (multipart form data)
     * @return the updated expense with receipt URL
     */
    @PostMapping("/{expenseId}/receipt")
    public ResponseEntity<ExpenseDto> uploadReceipt(
            Authentication authentication,
            @PathVariable Long expenseId,
            @RequestParam("file") MultipartFile file) {

        JwtUserDetails userDetails = (JwtUserDetails) authentication.getDetails();
        Long userId = userDetails.getUserId();

        log.info("API: POST /expenses/{}/receipt - User: {} - File: {}",
                expenseId, userId, file.getOriginalFilename());

        ExpenseDto updatedExpense = expenseService.uploadReceipt(userId, expenseId, file);
        return ResponseEntity.ok(updatedExpense);
    }
}