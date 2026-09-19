package com.spendsmart.income.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "incomes", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_date", columnList = "user_id,date"),
        @Index(name = "idx_user_source", columnList = "user_id,source"),
        @Index(name = "idx_user_category", columnList = "user_id,category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private Long incomeId;

    @NotNull(message = "User ID cannot be null")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false, length = 255)
    private String title;

    @NotNull(message = "Amount cannot be null")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank")
    @Column(nullable = false, length = 3)
    private String currency;

    @NotNull(message = "Source cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncomeSource source;

    @NotNull(message = "Date cannot be null")
    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_period", length = 20)
    private RecurrencePeriod recurrencePeriod;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum IncomeSource {
        SALARY, FREELANCE, BUSINESS, INVESTMENT, GIFT, OTHER
    }

    public enum RecurrencePeriod {
        DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    }
}
