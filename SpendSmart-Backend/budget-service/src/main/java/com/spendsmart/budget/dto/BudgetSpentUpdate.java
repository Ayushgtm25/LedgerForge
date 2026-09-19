package com.spendsmart.budget.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO used by expense-service to update the spent amount on a budget.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetSpentUpdate {

    @NotNull
    private Long userId;

    @NotNull
    private Long categoryId;

    @NotNull
    private BigDecimal amount;

    private String currency;
}
