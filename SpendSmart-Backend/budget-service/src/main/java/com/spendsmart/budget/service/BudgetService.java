package com.spendsmart.budget.service;

import com.spendsmart.budget.dto.BudgetRequest;
import com.spendsmart.budget.dto.BudgetResponse;
import com.spendsmart.budget.dto.BudgetSpentUpdate;

import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(Long userId, BudgetRequest request);

    BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request);

    void deleteBudget(Long userId, Long budgetId);

    BudgetResponse getBudgetById(Long userId, Long budgetId);

    List<BudgetResponse> getBudgetsByUser(Long userId);

    List<BudgetResponse> getActiveBudgets(Long userId);

    /** Called by expense-service to update spent amounts. */
    void updateSpentAmount(BudgetSpentUpdate update);

    /** Reset all monthly budgets (called by scheduler). */
    void resetMonthlyBudgets();

    List<BudgetResponse> getExceededBudgets(Long userId);

    long getBudgetCount(Long userId);
}
