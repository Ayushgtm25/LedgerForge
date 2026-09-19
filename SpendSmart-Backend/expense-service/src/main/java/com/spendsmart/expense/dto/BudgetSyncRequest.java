/**
 * BudgetSyncRequest is the payload sent to budget-service for spent amount updates.
 *
 * When an expense is created, updated (if amount/category changes), or deleted,
 * this request is sent to the budget-service to atomically update the budget's spent amount.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetSyncRequest {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("categoryId")
    private Long categoryId;

    /**
     * Delta amount: positive for additions, negative for deductions.
     * Serialized as {@code amount} to match {@code BudgetSpentUpdate} in budget-service.
     */
    @JsonProperty("amount")
    private BigDecimal deltaAmount;

    @JsonProperty("currency")
    private String currency;
}


