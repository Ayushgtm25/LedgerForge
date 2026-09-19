package com.spendsmart.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummaryResponse {

    private Long userId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalExpenses;
    private BigDecimal totalIncome;
    private BigDecimal netSavings;
    private Integer transactionCount;
    private String topExpenseCategory;
    private String topIncomeSource;
    private double savingsRate;
    private String currency;
}
