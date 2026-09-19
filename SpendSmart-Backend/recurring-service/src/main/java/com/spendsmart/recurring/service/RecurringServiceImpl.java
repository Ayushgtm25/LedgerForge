package com.spendsmart.recurring.service;

import com.spendsmart.recurring.dto.RecurringRuleRequest;
import com.spendsmart.recurring.dto.RecurringRuleResponse;
import com.spendsmart.recurring.entity.RecurringRule;
import com.spendsmart.recurring.entity.RecurringRule.*;
import com.spendsmart.recurring.exception.ResourceNotFoundException;
import com.spendsmart.recurring.repository.RecurringRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RecurringServiceImpl {

    private final RecurringRuleRepository ruleRepository;

    public RecurringRuleResponse createRule(Long userId, RecurringRuleRequest request) {
        log.info("Creating recurring rule '{}' for user {}", request.getTitle(), userId);

        RecurringRule rule = RecurringRule.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .title(request.getTitle())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .type(TransactionType.valueOf(request.getType()))
                .frequency(Frequency.valueOf(request.getFrequency()))
                .startDate(request.getStartDate())
                .nextDueDate(request.getStartDate())
                .endDate(request.getEndDate())
                .notes(request.getNotes())
                .build();

        rule = ruleRepository.save(rule);
        log.info("Recurring rule created: id={}", rule.getRuleId());
        return toResponse(rule);
    }

    public RecurringRuleResponse updateRule(Long userId, Long ruleId, RecurringRuleRequest request) {
        log.info("Updating recurring rule {} for user {}", ruleId, userId);
        RecurringRule rule = findByIdAndUser(ruleId, userId);

        rule.setCategoryId(request.getCategoryId());
        rule.setTitle(request.getTitle());
        rule.setAmount(request.getAmount());
        if (request.getCurrency() != null) rule.setCurrency(request.getCurrency());
        rule.setType(TransactionType.valueOf(request.getType()));
        rule.setFrequency(Frequency.valueOf(request.getFrequency()));
        rule.setStartDate(request.getStartDate());
        rule.setEndDate(request.getEndDate());
        rule.setNotes(request.getNotes());

        rule = ruleRepository.save(rule);
        log.info("Recurring rule updated: id={}", ruleId);
        return toResponse(rule);
    }

    public void deleteRule(Long userId, Long ruleId) {
        log.info("Deleting recurring rule {} for user {}", ruleId, userId);
        RecurringRule rule = findByIdAndUser(ruleId, userId);
        ruleRepository.delete(rule);
    }

    public RecurringRuleResponse toggleActive(Long userId, Long ruleId) {
        RecurringRule rule = findByIdAndUser(ruleId, userId);
        rule.setIsActive(!rule.getIsActive());
        rule = ruleRepository.save(rule);
        log.info("Rule {} active status toggled to {}", ruleId, rule.getIsActive());
        return toResponse(rule);
    }

    @Transactional(readOnly = true)
    public RecurringRuleResponse getRuleById(Long userId, Long ruleId) {
        return toResponse(findByIdAndUser(ruleId, userId));
    }

    @Transactional(readOnly = true)
    public List<RecurringRuleResponse> getRulesByUser(Long userId) {
        return ruleRepository.findByUserIdOrderByNextDueDateAsc(userId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RecurringRuleResponse> getActiveRules(Long userId) {
        return ruleRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RecurringRuleResponse> getDueRules() {
        return ruleRepository.findByIsActiveTrueAndNextDueDateLessThanEqual(LocalDate.now())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long getActiveCount(Long userId) {
        return ruleRepository.countByUserIdAndIsActiveTrue(userId);
    }

    private RecurringRule findByIdAndUser(Long ruleId, Long userId) {
        return ruleRepository.findByRuleIdAndUserId(ruleId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Recurring rule %d not found for user %d", ruleId, userId)));
    }

    private RecurringRuleResponse toResponse(RecurringRule rule) {
        return RecurringRuleResponse.builder()
                .ruleId(rule.getRuleId())
                .userId(rule.getUserId())
                .categoryId(rule.getCategoryId())
                .title(rule.getTitle())
                .amount(rule.getAmount())
                .currency(rule.getCurrency())
                .type(rule.getType().name())
                .frequency(rule.getFrequency().name())
                .startDate(rule.getStartDate())
                .nextDueDate(rule.getNextDueDate())
                .endDate(rule.getEndDate())
                .notes(rule.getNotes())
                .isActive(rule.getIsActive())
                .isDue(rule.isDue())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
