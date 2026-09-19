package com.spendsmart.recurring.repository;

import com.spendsmart.recurring.entity.RecurringRule;
import com.spendsmart.recurring.entity.RecurringRule.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringRuleRepository extends JpaRepository<RecurringRule, Long> {

    List<RecurringRule> findByUserIdOrderByNextDueDateAsc(Long userId);

    List<RecurringRule> findByUserIdAndIsActiveTrue(Long userId);

    List<RecurringRule> findByUserIdAndType(Long userId, TransactionType type);

    Optional<RecurringRule> findByRuleIdAndUserId(Long ruleId, Long userId);

    List<RecurringRule> findByIsActiveTrueAndNextDueDateLessThanEqual(LocalDate date);

    long countByUserId(Long userId);

    long countByUserIdAndIsActiveTrue(Long userId);
}
