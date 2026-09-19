package com.spendsmart.budget.service;

import com.spendsmart.budget.dto.BudgetRequest;
import com.spendsmart.budget.dto.BudgetResponse;
import com.spendsmart.budget.dto.BudgetSpentUpdate;
import com.spendsmart.budget.entity.Budget;
import com.spendsmart.budget.entity.Budget.BudgetPeriod;
import com.spendsmart.budget.exception.ResourceNotFoundException;
import com.spendsmart.budget.mapper.BudgetMapper;
import com.spendsmart.budget.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    @Override
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        log.info("Creating budget '{}' for user {}", request.getName(), userId);

        Budget budget = Budget.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .budgetLimit(request.getBudgetLimit())
                .alertThreshold(request.getAlertThreshold() != null ? request.getAlertThreshold() : 80)
                .period(BudgetPeriod.valueOf(request.getPeriod()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .isActive(true)
                .build();

        budget = budgetRepository.save(budget);
        log.info("Budget created: id={}", budget.getBudgetId());
        return BudgetMapper.toResponse(budget);
    }

    @Override
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        log.info("Updating budget {} for user {}", budgetId, userId);

        Budget budget = findByIdAndUser(budgetId, userId);

        budget.setName(request.getName());
        budget.setBudgetLimit(request.getBudgetLimit());
        budget.setCategoryId(request.getCategoryId());
        if (request.getAlertThreshold() != null) budget.setAlertThreshold(request.getAlertThreshold());
        if (request.getPeriod() != null) budget.setPeriod(BudgetPeriod.valueOf(request.getPeriod()));
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        if (request.getCurrency() != null) budget.setCurrency(request.getCurrency());

        budget = budgetRepository.save(budget);
        log.info("Budget updated: id={}", budgetId);
        return BudgetMapper.toResponse(budget);
    }

    @Override
    public void deleteBudget(Long userId, Long budgetId) {
        log.info("Deleting budget {} for user {}", budgetId, userId);
        Budget budget = findByIdAndUser(budgetId, userId);
        budgetRepository.delete(budget);
        log.info("Budget deleted: id={}", budgetId);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long userId, Long budgetId) {
        return BudgetMapper.toResponse(findByIdAndUser(budgetId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BudgetMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getActiveBudgets(Long userId) {
        return budgetRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(BudgetMapper::toResponse).toList();
    }

    @Override
    public void updateSpentAmount(BudgetSpentUpdate update) {
        log.info("Updating spent amount for user {} category {} by {}",
                update.getUserId(), update.getCategoryId(), update.getAmount());

        budgetRepository.findByUserIdAndCategoryIdAndIsActiveTrue(
                update.getUserId(), update.getCategoryId()
        ).ifPresent(budget -> {
            budget.setSpentAmount(budget.getSpentAmount().add(update.getAmount()));

            // Prevent negative spent amounts
            if (budget.getSpentAmount().compareTo(BigDecimal.ZERO) < 0) {
                budget.setSpentAmount(BigDecimal.ZERO);
            }

            budgetRepository.save(budget);
            log.info("Budget {} spent updated to {}", budget.getBudgetId(), budget.getSpentAmount());

            // Log threshold warnings
            if (budget.isOverBudget()) {
                log.warn("BUDGET EXCEEDED: Budget '{}' (id={}) is over limit. Spent: {}, Limit: {}",
                        budget.getName(), budget.getBudgetId(), budget.getSpentAmount(), budget.getBudgetLimit());
            } else if (budget.isThresholdExceeded()) {
                log.warn("BUDGET THRESHOLD: Budget '{}' (id={}) reached {}% of limit",
                        budget.getName(), budget.getBudgetId(), String.format("%.1f", budget.getUsagePercentage()));
            }
        });
    }

    @Override
    public void resetMonthlyBudgets() {
        log.info("Resetting monthly budgets...");
        List<Budget> monthlyBudgets = budgetRepository.findAll().stream()
                .filter(b -> b.getPeriod() == BudgetPeriod.MONTHLY && b.getIsActive())
                .toList();

        for (Budget budget : monthlyBudgets) {
            budget.setSpentAmount(BigDecimal.ZERO);
            budgetRepository.save(budget);
        }
        log.info("Reset {} monthly budgets", monthlyBudgets.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getExceededBudgets(Long userId) {
        return budgetRepository.findExceededThresholdBudgets(userId).stream()
                .map(BudgetMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getBudgetCount(Long userId) {
        return budgetRepository.countByUserId(userId);
    }

    private Budget findByIdAndUser(Long budgetId, Long userId) {
        return budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Budget %d not found for user %d", budgetId, userId)));
    }
}
