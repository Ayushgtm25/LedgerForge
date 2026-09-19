package com.spendsmart.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrend {

    private int year;
    private int month;
    private String monthName;
    private BigDecimal totalExpenses;
    private BigDecimal totalIncome;
    private BigDecimal netSavings;
    private Integer transactionCount;
}
