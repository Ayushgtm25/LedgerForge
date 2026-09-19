package com.spendsmart.budget.repository;

import com.spendsmart.budget.entity.Budget;
import com.spendsmart.budget.entity.Budget.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Budget> findByUserIdAndIsActiveTrue(Long userId);

    Optional<Budget> findByBudgetIdAndUserId(Long budgetId, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndIsActiveTrue(Long userId, Long categoryId);

    List<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);

    long countByUserId(Long userId);

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.isActive = true " +
           "AND (b.spentAmount * 100.0 / b.budgetLimit) >= b.alertThreshold")
    List<Budget> findExceededThresholdBudgets(@Param("userId") Long userId);

    @Query("SELECT b FROM Budget b WHERE b.isActive = true " +
           "AND b.spentAmount > b.budgetLimit")
    List<Budget> findAllOverBudget();
}
