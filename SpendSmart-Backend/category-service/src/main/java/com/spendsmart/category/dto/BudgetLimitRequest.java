package com.spendsmart.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetLimitRequest {

    @NotNull(message = "Budget limit is required")
    @PositiveOrZero(message = "Budget limit must be zero or positive")
    private BigDecimal budgetLimit;
}
