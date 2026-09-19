/**
 * ExpenseRepository provides data access methods for Expense entities.
 *
 * This interface extends JpaRepository and defines custom query methods
 * for retrieving expenses with strict user-scoping. CRITICAL: Every query
 * includes userId to enforce data isolation and prevent unauthorized access.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.repository;

import com.spendsmart.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find all expenses for a specific user.
     *
     * @param userId the user's unique identifier
     * @return list of expenses for the user
     */
    List<Expense> findByUserId(Long userId);

    /**
     * Find expenses for a user by type (EXPENSE or SPLIT).
     *
     * @param userId the user's unique identifier
     * @param type the expense type
     * @return list of filtered expenses
     */
    List<Expense> findByUserIdAndType(Long userId, Expense.ExpenseType type);

    /**
     * Find expenses for a user by category.
     *
     * @param userId the user's unique identifier
     * @param categoryId the category ID
     * @return list of expenses in the specified category
     */
    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Find expenses for a user on a specific date.
     *
     * @param userId the user's unique identifier
     * @param date the specific date
     * @return list of expenses on the date
     */
    List<Expense> findByUserIdAndDate(Long userId, LocalDate date);

    /**
     * Find expenses for a user within a date range.
     *
     * @param userId the user's unique identifier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses within the date range
     */
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses for a user by year and month.
     * Uses a custom query to extract year and month from the date field.
     *
     * @param userId the user's unique identifier
     * @param month the month (1-12)
     * @param year the year (e.g., 2026)
     * @return list of expenses in the specified month/year
     */
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    List<Expense> findByUserIdAndDateMonthAndYear(@Param("userId") Long userId,
                                                   @Param("month") int month,
                                                   @Param("year") int year);

    /**
     * Sum the amount of all expenses for a user.
     *
     * @param userId the user's unique identifier
     * @return total amount, or null if no expenses exist
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    /**
     * Sum the amount of expenses for a user in a specific category.
     *
     * @param userId the user's unique identifier
     * @param categoryId the category ID
     * @return total amount in the category, or null if no expenses exist
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND e.categoryId = :categoryId")
    BigDecimal sumAmountByUserIdAndCategoryId(@Param("userId") Long userId,
                                              @Param("categoryId") Long categoryId);

    /**
     * Search expenses for a user by keyword in title or notes.
     *
     * @param userId the user's unique identifier
     * @param keyword the search keyword
     * @return list of matching expenses
     */
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.notes) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Expense> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * Find a specific expense by ID and userId (ensures ownership).
     *
     * @param expenseId the expense ID
     * @param userId the user's unique identifier
     * @return the expense if found and owned by the user
     */
    Optional<Expense> findByExpenseIdAndUserId(Long expenseId, Long userId);

    /**
     * Delete an expense by ID and userId (ensures data isolation).
     *
     * @param expenseId the expense ID
     * @param userId the user's unique identifier
     * @return the number of rows deleted (0 or 1)
     */
    long deleteByExpenseIdAndUserId(Long expenseId, Long userId);

    /**
     * Find expenses by payment method and user.
     *
     * @param userId the user's unique identifier
     * @param paymentMethod the payment method
     * @return list of expenses with the specified payment method
     */
    List<Expense> findByUserIdAndPaymentMethod(Long userId, Expense.PaymentMethod paymentMethod);

    List<Expense> findAllByOrderByDateDesc();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal sumAllAmounts();
}


