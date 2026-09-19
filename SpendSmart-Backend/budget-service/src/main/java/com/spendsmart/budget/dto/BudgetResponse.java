package com.spendsmart.budget.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private Long budgetId;
    private Long userId;
    private Long categoryId;
    private String name;
    private BigDecimal budgetLimit;
    private BigDecimal spentAmount;
    private Integer alertThreshold;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currency;
    private Boolean isActive;
    private double usagePercentage;
    private boolean thresholdExceeded;
    private boolean overBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
