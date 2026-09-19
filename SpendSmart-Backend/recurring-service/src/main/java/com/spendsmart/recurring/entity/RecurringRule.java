package com.spendsmart.recurring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "recurring_rules",
    indexes = {
        @Index(name = "idx_recurring_user", columnList = "user_id"),
        @Index(name = "idx_recurring_active", columnList = "is_active"),
        @Index(name = "idx_recurring_next_due", columnList = "next_due_date"),
        @Index(name = "idx_recurring_type", columnList = "type")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 500)
    private String notes;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum TransactionType {
        EXPENSE,
        INCOME
    }

    public enum Frequency {
        DAILY,
        WEEKLY,
        BIWEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY
    }

    /**
     * Calculate the next due date based on the current frequency.
     */
    public LocalDate calculateNextDueDate() {
        return switch (frequency) {
            case DAILY -> nextDueDate.plusDays(1);
            case WEEKLY -> nextDueDate.plusWeeks(1);
            case BIWEEKLY -> nextDueDate.plusWeeks(2);
            case MONTHLY -> nextDueDate.plusMonths(1);
            case QUARTERLY -> nextDueDate.plusMonths(3);
            case YEARLY -> nextDueDate.plusYears(1);
        };
    }

    /**
     * Check if this rule is due (on or before today).
     */
    public boolean isDue() {
        return isActive && !nextDueDate.isAfter(LocalDate.now());
    }
}
