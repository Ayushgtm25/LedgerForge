package com.spendsmart.budget.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Budget entity representing a spending limit for a category or overall spending.
 * Tracks budget limits and actual spent amounts for threshold alerts.
 */
@Entity
@Table(
    name = "budgets",
    indexes = {
        @Index(name = "idx_budget_user_id", columnList = "user_id"),
        @Index(name = "idx_budget_user_category", columnList = "user_id, category_id"),
        @Index(name = "idx_budget_period", columnList = "period"),
        @Index(name = "idx_budget_active", columnList = "is_active")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** The category this budget applies to. Null means overall budget. */
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 100)
    private String name;

    /** Maximum spending limit for this budget period. */
    @Column(name = "budget_limit", nullable = false, precision = 12, scale = 2)
    private BigDecimal budgetLimit;

    /** Actual amount spent so far within the current period. */
    @Column(name = "spent_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal spentAmount = BigDecimal.ZERO;

    /** Alert threshold percentage (e.g. 80 means alert at 80% spent). */
    @Column(name = "alert_threshold", nullable = false)
    @Builder.Default
    private Integer alertThreshold = 80;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BudgetPeriod period = BudgetPeriod.MONTHLY;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum BudgetPeriod {
        WEEKLY,
        MONTHLY,
        YEARLY
    }

    /**
     * Calculate the percentage of budget used.
     */
    public double getUsagePercentage() {
        if (budgetLimit.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return spentAmount.doubleValue() / budgetLimit.doubleValue() * 100.0;
    }

    /**
     * Check if the budget has exceeded the alert threshold.
     */
    public boolean isThresholdExceeded() {
        return getUsagePercentage() >= alertThreshold;
    }

    /**
     * Check if the budget limit has been exceeded.
     */
    public boolean isOverBudget() {
        return spentAmount.compareTo(budgetLimit) > 0;
    }
}
