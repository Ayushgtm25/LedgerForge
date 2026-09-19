/**
 * ExpenseServiceImplTest provides comprehensive unit tests for ExpenseServiceImpl.
 *
 * Tests cover:
 * - CRUD operations
 * - Budget synchronization triggers
 * - Date range filtering
 * - User data isolation
 * - Error handling and edge cases
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
import com.spendsmart.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private BudgetSyncService budgetSyncService;

    @Mock
    private ReceiptStorageService receiptStorageService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Long userId = 1L;
    private Long expenseId = 100L;
    private ExpenseDto testExpenseDto;
    private Expense testExpense;

    @BeforeEach
    void setUp() {
        // Create test data
        testExpenseDto = ExpenseDto.builder()
                .categoryId(10L)
                .title("Grocery Shopping")
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .type("EXPENSE")
                .paymentMethod("CARD")
                .date(LocalDate.now())
                .notes("Weekly groceries")
                .isRecurring(false)
                .build();

        testExpense = Expense.builder()
                .expenseId(expenseId)
                .userId(userId)
                .categoryId(10L)
                .title("Grocery Shopping")
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .type(Expense.ExpenseType.EXPENSE)
                .paymentMethod(Expense.PaymentMethod.CARD)
                .date(LocalDate.now())
                .notes("Weekly groceries")
                .isRecurring(false)
                .build();
    }

    @Test
    void testAddExpense_Success() {
        // Arrange
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseDto result = expenseService.addExpense(userId, testExpenseDto);

        // Assert
        assertNotNull(result);
        assertEquals(testExpenseDto.getTitle(), result.getTitle());
        assertEquals(testExpenseDto.getAmount(), result.getAmount());

        // Verify budget sync was called
        verify(budgetSyncService, times(1)).syncBudgetSpentAmount(
                userId, 10L, new BigDecimal("50.00"), "USD"
        );
    }

    @Test
    void testGetExpenseById_Success() {
        // Arrange
        when(expenseRepository.findByExpenseIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(testExpense));

        // Act
        ExpenseDto result = expenseService.getExpenseById(userId, expenseId);

        // Assert
        assertNotNull(result);
        assertEquals(testExpense.getTitle(), result.getTitle());
    }

    @Test
    void testGetExpenseById_NotFound() {
        // Arrange
        when(expenseRepository.findByExpenseIdAndUserId(expenseId, userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class,
                () -> expenseService.getExpenseById(userId, expenseId));
    }

    @Test
    void testDeleteExpense_Success() {
        // Arrange
        when(expenseRepository.findByExpenseIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(testExpense));
        when(expenseRepository.deleteByExpenseIdAndUserId(expenseId, userId))
                .thenReturn(1L);

        // Act
        expenseService.deleteExpense(userId, expenseId);

        // Assert
        verify(expenseRepository, times(1)).deleteByExpenseIdAndUserId(expenseId, userId);

        // Verify budget sync was called with negative amount
        verify(budgetSyncService, times(1)).syncBudgetSpentAmount(
                userId, 10L, new BigDecimal("-50.00"), "USD"
        );
    }

    @Test
    void testUpdateExpense_AmountChanged() {
        // Arrange
        UpdateExpenseDto updateDto = UpdateExpenseDto.builder()
                .amount(new BigDecimal("75.00"))
                .build();

        when(expenseRepository.findByExpenseIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        expenseService.updateExpense(userId, expenseId, updateDto);

        // Assert
        // Verify budget sync was called with delta amount (75 - 50 = 25)
        verify(budgetSyncService, times(1)).syncBudgetSpentAmount(
                userId, 10L, new BigDecimal("25.00"), "USD"
        );
    }

    @Test
    void testGetExpensesByDateRange_Valid() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        List<Expense> expenses = Arrays.asList(testExpense);

        when(expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate))
                .thenReturn(expenses);

        // Act
        List<ExpenseDto> result = expenseService.getExpensesByDateRange(userId, startDate, endDate);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetExpensesByDateRange_InvalidRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 12, 31);
        LocalDate endDate = LocalDate.of(2026, 1, 1);

        // Act & Assert
        assertThrows(InvalidDateRangeException.class,
                () -> expenseService.getExpensesByDateRange(userId, startDate, endDate));
    }

    @Test
    void testGetTotalByUser() {
        // Arrange
        BigDecimal expectedTotal = new BigDecimal("100.00");
        when(expenseRepository.sumAmountByUserId(userId)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = expenseService.getTotalByUser(userId);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void testGetTotalByUser_ZeroWhenNoExpenses() {
        // Arrange
        when(expenseRepository.sumAmountByUserId(userId)).thenReturn(null);

        // Act
        BigDecimal result = expenseService.getTotalByUser(userId);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testSearchExpenses() {
        // Arrange
        String keyword = "grocery";
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.searchByKeyword(userId, keyword)).thenReturn(expenses);

        // Act
        List<ExpenseDto> result = expenseService.searchExpenses(userId, keyword);

        // Assert
        assertEquals(1, result.size());
    }
}


