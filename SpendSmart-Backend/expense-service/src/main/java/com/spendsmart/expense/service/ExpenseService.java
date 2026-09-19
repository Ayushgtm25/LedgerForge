/**
 * ExpenseService defines the business logic interface for expense management.
 *
 * All methods include user-scoped data access for security. Budget synchronization
 * is handled internally when expenses are created, updated, or deleted.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.service;

import com.spendsmart.expense.dto.ExpenseDto;
import com.spendsmart.expense.dto.UpdateExpenseDto;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    /**
     * Add a new expense for the user.
     * Triggers budget synchronization with the budget-service.
     *
     * @param userId the authenticated user's ID
     * @param expenseDto the expense details
     * @return the created expense
     */
    ExpenseDto addExpense(Long userId, ExpenseDto expenseDto);

    /**
     * Retrieve a specific expense by ID (with ownership validation).
     *
     * @param userId the authenticated user's ID
     * @param expenseId the expense ID
     * @return the expense
     * @throws ExpenseNotFoundException if not found
     * @throws UnauthorizedAccessException if user doesn't own the expense
     */
    ExpenseDto getExpenseById(Long userId, Long expenseId);

    /**
     * Get all expenses for a user.
     *
     * @param userId the authenticated user's ID
     * @return list of expenses
     */
    List<ExpenseDto> getExpensesByUser(Long userId);

    /**
     * Get expenses filtered by category.
     *
     * @param userId the authenticated user's ID
     * @param categoryId the category ID
     * @return list of matching expenses
     */
    List<ExpenseDto> getExpensesByCategory(Long userId, Long categoryId);

    /**
     * Get expenses within a date range.
     *
     * @param userId the authenticated user's ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of matching expenses
     * @throws InvalidDateRangeException if dates are invalid
     */
    List<ExpenseDto> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Get expenses for a specific month/year.
     *
     * @param userId the authenticated user's ID
     * @param month the month (1-12)
     * @param year the year
     * @return list of expenses in the month/year
     */
    List<ExpenseDto> getExpensesByMonth(Long userId, int month, int year);

    /**
     * Update an expense (can update any field).
     * Triggers budget synchronization if amount or category changed.
     *
     * @param userId the authenticated user's ID
     * @param expenseId the expense ID
     * @param updateDto the updated fields (all optional)
     * @return the updated expense
     * @throws ExpenseNotFoundException if not found
     * @throws UnauthorizedAccessException if user doesn't own the expense
     */
    ExpenseDto updateExpense(Long userId, Long expenseId, UpdateExpenseDto updateDto);

    /**
     * Delete an expense.
     * Triggers budget synchronization to decrement spent amount.
     *
     * @param userId the authenticated user's ID
     * @param expenseId the expense ID
     * @throws ExpenseNotFoundException if not found
     * @throws UnauthorizedAccessException if user doesn't own the expense
     */
    void deleteExpense(Long userId, Long expenseId);

    /**
     * Get the total amount spent by a user.
     *
     * @param userId the authenticated user's ID
     * @return total amount, or zero if no expenses
     */
    BigDecimal getTotalByUser(Long userId);

    /**
     * Get the total spent in a specific category.
     *
     * @param userId the authenticated user's ID
     * @param categoryId the category ID
     * @return total amount, or zero if no expenses
     */
    BigDecimal getTotalByCategory(Long userId, Long categoryId);

    /**
     * Get expenses filtered by type (EXPENSE or SPLIT).
     *
     * @param userId the authenticated user's ID
     * @param type the expense type
     * @return list of matching expenses
     */
    List<ExpenseDto> getExpensesByType(Long userId, String type);

    /**
     * Get expenses filtered by payment method.
     *
     * @param userId the authenticated user's ID
     * @param paymentMethod the payment method
     * @return list of matching expenses
     */
    List<ExpenseDto> getExpensesByPaymentMethod(Long userId, String paymentMethod);

    /**
     * Search expenses by keyword in title or notes.
     *
     * @param userId the authenticated user's ID
     * @param keyword the search keyword
     * @return list of matching expenses
     */
    List<ExpenseDto> searchExpenses(Long userId, String keyword);

    /**
     * Upload a receipt file and attach to expense.
     * Uploads to AWS S3 (mocked for development).
     *
     * @param userId the authenticated user's ID
     * @param expenseId the expense ID
     * @param file the multipart file
     * @return the updated expense with receipt URL
     * @throws ExpenseNotFoundException if not found
     * @throws UnauthorizedAccessException if user doesn't own the expense
     */
    ExpenseDto uploadReceipt(Long userId, Long expenseId, MultipartFile file);

    List<ExpenseDto> getAllExpenses();

    long getTotalExpenseCount();

    BigDecimal getTotalAcrossAllUsers();
}


