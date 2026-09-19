/**
 * ExpenseServiceImpl implements the business logic for expense management.
 *
 * This service:
 * - Maps entities to DTOs using MapStruct mappers
 * - Enforces user-scoped data isolation
 * - Triggers budget synchronization for CRUD operations
 * - Handles S3 receipt uploads
 * - Provides comprehensive logging
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.service;

import com.spendsmart.expense.dto.ExpenseDto;
import com.spendsmart.expense.dto.UpdateExpenseDto;
import com.spendsmart.expense.entity.Expense;
import com.spendsmart.expense.exception.ExpenseNotFoundException;
import com.spendsmart.expense.exception.InvalidDateRangeException;
import com.spendsmart.expense.exception.UnauthorizedAccessException;
import com.spendsmart.expense.repository.ExpenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetSyncService budgetSyncService;

    @Autowired
    private ReceiptStorageService receiptStorageService;

    @Override
    public ExpenseDto addExpense(Long userId, ExpenseDto expenseDto) {
        log.info("Adding new expense for user: {}, title: {}, amount: {}",
                userId, expenseDto.getTitle(), expenseDto.getAmount());

        try {
            // Map DTO to entity
            Expense expense = mapDtoToEntity(expenseDto);
            expense.setUserId(userId);

            // Save to database
            Expense savedExpense = expenseRepository.save(expense);
            log.debug("Expense saved with ID: {}", savedExpense.getExpenseId());

            // Sync with budget-service
            budgetSyncService.syncBudgetSpentAmount(
                    userId,
                    expense.getCategoryId(),
                    expense.getAmount(),
                    expense.getCurrency()
            );

            log.info("Expense created successfully: ID {}", savedExpense.getExpenseId());

            return mapEntityToDto(savedExpense);

        } catch (Exception ex) {
            log.error("Error adding expense for user {}: {}", userId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public ExpenseDto getExpenseById(Long userId, Long expenseId) {
        log.debug("Fetching expense {} for user {}", expenseId, userId);

        Optional<Expense> expense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId);

        if (expense.isEmpty()) {
            log.warn("Expense {} not found for user {}", expenseId, userId);
            throw new ExpenseNotFoundException(expenseId);
        }

        return mapEntityToDto(expense.get());
    }

    @Override
    public List<ExpenseDto> getExpensesByUser(Long userId) {
        log.debug("Fetching all expenses for user {}", userId);

        List<Expense> expenses = expenseRepository.findByUserId(userId);
        log.debug("Found {} expenses for user {}", expenses.size(), userId);

        return expenses.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDto> getExpensesByCategory(Long userId, Long categoryId) {
        log.debug("Fetching expenses for user {} in category {}", userId, categoryId);

        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        log.debug("Found {} expenses in category {}", expenses.size(), categoryId);

        return expenses.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDto> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching expenses for user {} between {} and {}", userId, startDate, endDate);

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date cannot be after end date");
        }

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        log.debug("Found {} expenses in date range", expenses.size());

        return expenses.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDto> getExpensesByMonth(Long userId, int month, int year) {
        log.debug("Fetching expenses for user {} in {}/{}", userId, month, year);

        // Validate month
        if (month < 1 || month > 12) {
            throw new InvalidDateRangeException("Month must be between 1 and 12");
        }

        List<Expense> expenses = expenseRepository.findByUserIdAndDateMonthAndYear(userId, month, year);
        log.debug("Found {} expenses in {}/{}", expenses.size(), month, year);

        return expenses.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseDto updateExpense(Long userId, Long expenseId, UpdateExpenseDto updateDto) {
        log.info("Updating expense {} for user {}", expenseId, userId);

        // Fetch existing expense (with ownership check)
        Expense existingExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        // Track old amount and category for budget sync
        BigDecimal oldAmount = existingExpense.getAmount();
        Long oldCategoryId = existingExpense.getCategoryId();

        try {
            // Apply updates (only non-null fields)
            if (updateDto.getCategoryId() != null) {
                existingExpense.setCategoryId(updateDto.getCategoryId());
            }
            if (updateDto.getTitle() != null) {
                existingExpense.setTitle(updateDto.getTitle());
            }
            if (updateDto.getAmount() != null) {
                existingExpense.setAmount(updateDto.getAmount());
            }
            if (updateDto.getCurrency() != null) {
                existingExpense.setCurrency(updateDto.getCurrency());
            }
            if (updateDto.getType() != null) {
                existingExpense.setType(Expense.ExpenseType.valueOf(updateDto.getType()));
            }
            if (updateDto.getPaymentMethod() != null) {
                existingExpense.setPaymentMethod(Expense.PaymentMethod.valueOf(updateDto.getPaymentMethod()));
            }
            if (updateDto.getDate() != null) {
                existingExpense.setDate(updateDto.getDate());
            }
            if (updateDto.getNotes() != null) {
                existingExpense.setNotes(updateDto.getNotes());
            }
            if (updateDto.getIsRecurring() != null) {
                existingExpense.setIsRecurring(updateDto.getIsRecurring());
            }

            // Save updated expense
            Expense updatedExpense = expenseRepository.save(existingExpense);
            log.debug("Expense {} updated in database", expenseId);

            // Sync budget if amount or category changed
            if (!oldAmount.equals(existingExpense.getAmount()) ||
                !oldCategoryId.equals(existingExpense.getCategoryId())) {

                BigDecimal deltaAmount = existingExpense.getAmount().subtract(oldAmount);
                budgetSyncService.syncBudgetSpentAmount(
                        userId,
                        existingExpense.getCategoryId(),
                        deltaAmount,
                        existingExpense.getCurrency()
                );
                log.debug("Budget synced with delta: {}", deltaAmount);
            }

            log.info("Expense {} updated successfully", expenseId);
            return mapEntityToDto(updatedExpense);

        } catch (Exception ex) {
            log.error("Error updating expense {}: {}", expenseId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        log.info("Deleting expense {} for user {}", expenseId, userId);

        // Fetch expense to verify ownership and get amount for budget sync
        Expense expense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        try {
            // Delete from database
            expenseRepository.deleteByExpenseIdAndUserId(expenseId, userId);
            log.debug("Expense {} deleted from database", expenseId);

            // Sync budget - negative amount to decrement spent
            budgetSyncService.syncBudgetSpentAmount(
                    userId,
                    expense.getCategoryId(),
                    expense.getAmount().negate(),
                    expense.getCurrency()
            );

            log.info("Expense {} deleted successfully", expenseId);

        } catch (Exception ex) {
            log.error("Error deleting expense {}: {}", expenseId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public BigDecimal getTotalByUser(Long userId) {
        log.debug("Calculating total spent by user {}", userId);

        BigDecimal total = expenseRepository.sumAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalByCategory(Long userId, Long categoryId) {
        log.debug("Calculating total spent in category {} for user {}", categoryId, userId);

        BigDecimal total = expenseRepository.sumAmountByUserIdAndCategoryId(userId, categoryId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<ExpenseDto> getExpensesByType(Long userId, String type) {
        log.debug("Fetching {} expenses for user {}", type, userId);

        try {
            Expense.ExpenseType expenseType = Expense.ExpenseType.valueOf(type.toUpperCase());
            List<Expense> expenses = expenseRepository.findByUserIdAndType(userId, expenseType);
            log.debug("Found {} expenses of type {}", expenses.size(), type);

            return expenses.stream()
                    .map(this::mapEntityToDto)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException ex) {
            log.warn("Invalid expense type: {}", type);
            throw ex;
        }
    }

    @Override
    public List<ExpenseDto> getExpensesByPaymentMethod(Long userId, String paymentMethod) {
        log.debug("Fetching expenses with payment method {} for user {}", paymentMethod, userId);

        try {
            Expense.PaymentMethod method = Expense.PaymentMethod.valueOf(paymentMethod.toUpperCase());
            List<Expense> expenses = expenseRepository.findByUserIdAndPaymentMethod(userId, method);
            log.debug("Found {} expenses with payment method {}", expenses.size(), paymentMethod);

            return expenses.stream()
                    .map(this::mapEntityToDto)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException ex) {
            log.warn("Invalid payment method: {}", paymentMethod);
            throw ex;
        }
    }

    @Override
    public List<ExpenseDto> searchExpenses(Long userId, String keyword) {
        log.info("Searching expenses for user {} with keyword: {}", userId, keyword);

        List<Expense> expenses = expenseRepository.searchByKeyword(userId, keyword);
        log.debug("Found {} matching expenses", expenses.size());

        return expenses.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseDto uploadReceipt(Long userId, Long expenseId, MultipartFile file) {
        log.info("Uploading receipt for expense {} by user {}", expenseId, userId);

        // Fetch expense (with ownership check)
        Expense expense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        try {
            // Upload to storage and get URL
            String receiptUrl = receiptStorageService.uploadReceipt(userId, expenseId, file);
            log.debug("Receipt URL: {}", receiptUrl);

            // Update expense with receipt URL
            expense.setReceiptUrl(receiptUrl);
            Expense updatedExpense = expenseRepository.save(expense);

            log.info("Receipt uploaded successfully for expense {}", expenseId);
            return mapEntityToDto(updatedExpense);

        } catch (IOException ex) {
            log.error("Failed to upload receipt for expense {}: {}", expenseId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to upload receipt: " + ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDto> getAllExpenses() {
        return expenseRepository.findAllByOrderByDateDesc().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalExpenseCount() {
        return expenseRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAcrossAllUsers() {
        return expenseRepository.sumAllAmounts();
    }

    // ============ Helper Methods ============

    /**
     * Map Expense entity to ExpenseDto.
     */
    private ExpenseDto mapEntityToDto(Expense expense) {
        return ExpenseDto.builder()
                .expenseId(expense.getExpenseId())
                .categoryId(expense.getCategoryId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .currency(expense.getCurrency())
                .type(expense.getType().toString())
                .paymentMethod(expense.getPaymentMethod().toString())
                .date(expense.getDate())
                .notes(expense.getNotes())
                .receiptUrl(expense.getReceiptUrl())
                .isRecurring(expense.getIsRecurring())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    /**
     * Map ExpenseDto to Expense entity.
     */
    private Expense mapDtoToEntity(ExpenseDto dto) {
        return Expense.builder()
                .categoryId(dto.getCategoryId())
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .type(Expense.ExpenseType.valueOf(dto.getType()))
                .paymentMethod(Expense.PaymentMethod.valueOf(dto.getPaymentMethod()))
                .date(dto.getDate())
                .notes(dto.getNotes())
                .receiptUrl(dto.getReceiptUrl())
                .isRecurring(dto.getIsRecurring())
                .build();
    }
}


