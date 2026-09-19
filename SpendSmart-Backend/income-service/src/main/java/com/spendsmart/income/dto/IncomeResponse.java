package com.spendsmart.income.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeResponse {

    private Long incomeId;
    private Long userId;
    private Long categoryId;
    private String title;
    private BigDecimal amount;
    private String currency;
    private String source;
    private LocalDate date;
    private String notes;
    private Boolean isRecurring;
    private String recurrencePeriod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
