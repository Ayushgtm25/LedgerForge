package com.spendsmart.budget.mapper;

import com.spendsmart.budget.dto.BudgetResponse;
import com.spendsmart.budget.entity.Budget;

public class BudgetMapper {

    private BudgetMapper() {}

    public static BudgetResponse toResponse(Budget budget) {
        return BudgetResponse.builder()
                .budgetId(budget.getBudgetId())
                .userId(budget.getUserId())
                .categoryId(budget.getCategoryId())
                .name(budget.getName())
                .budgetLimit(budget.getBudgetLimit())
                .spentAmount(budget.getSpentAmount())
                .alertThreshold(budget.getAlertThreshold())
                .period(budget.getPeriod().name())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .currency(budget.getCurrency())
                .isActive(budget.getIsActive())
                .usagePercentage(budget.getUsagePercentage())
                .thresholdExceeded(budget.isThresholdExceeded())
                .overBudget(budget.isOverBudget())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}
