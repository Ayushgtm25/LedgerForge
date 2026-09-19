package com.spendsmart.income.service;

import com.spendsmart.income.dto.IncomeRequest;
import com.spendsmart.income.dto.IncomeResponse;
import com.spendsmart.income.entity.Income;
import com.spendsmart.income.entity.Income.IncomeSource;
import com.spendsmart.income.exception.ResourceNotFoundException;
import com.spendsmart.income.mapper.IncomeMapper;
import com.spendsmart.income.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Override
    public IncomeResponse createIncome(Long userId, IncomeRequest request) {
        log.info("Creating income '{}' for user {}", request.getTitle(), userId);

        Income income = Income.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .title(request.getTitle())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .source(request.getSource())
                .date(request.getDate())
                .notes(request.getNotes())
                .isRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false)
                .recurrencePeriod(request.getRecurrencePeriod())
                .build();

        income = incomeRepository.save(income);
        log.info("Income created: id={}", income.getIncomeId());
        return IncomeMapper.toResponse(income);
    }

    @Override
    public IncomeResponse updateIncome(Long userId, Long incomeId, IncomeRequest request) {
        log.info("Updating income {} for user {}", incomeId, userId);

        Income income = findByIdAndUser(incomeId, userId);
        income.setCategoryId(request.getCategoryId());
        income.setTitle(request.getTitle());
        income.setAmount(request.getAmount());
        if (request.getCurrency() != null) income.setCurrency(request.getCurrency());
        income.setSource(request.getSource());
        income.setDate(request.getDate());
        income.setNotes(request.getNotes());
        income.setIsRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false);
        income.setRecurrencePeriod(request.getRecurrencePeriod());

        income = incomeRepository.save(income);
        log.info("Income updated: id={}", incomeId);
        return IncomeMapper.toResponse(income);
    }

    @Override
    public void deleteIncome(Long userId, Long incomeId) {
        log.info("Deleting income {} for user {}", incomeId, userId);
        Income income = findByIdAndUser(incomeId, userId);
        incomeRepository.delete(income);
        log.info("Income deleted: id={}", incomeId);
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeResponse getIncomeById(Long userId, Long incomeId) {
        return IncomeMapper.toResponse(findByIdAndUser(incomeId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomesByUser(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(IncomeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomesBySource(Long userId, IncomeSource source) {
        return incomeRepository.findByUserIdAndSource(userId, source).stream()
                .map(IncomeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomesByDateRange(Long userId, LocalDate from, LocalDate to) {
        return incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, from, to).stream()
                .map(IncomeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> searchIncomes(Long userId, String keyword) {
        return incomeRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByDateDesc(userId, keyword).stream()
                .map(IncomeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(Long userId) {
        return incomeRepository.sumAmountByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncomeBySource(Long userId, IncomeSource source) {
        return incomeRepository.sumAmountByUserIdAndSource(userId, source);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncomeByDateRange(Long userId, LocalDate from, LocalDate to) {
        return incomeRepository.sumAmountByUserIdAndDateBetween(userId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getIncomeTotals(Long userId) {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        totals.put("total", getTotalIncome(userId));
        for (IncomeSource source : IncomeSource.values()) {
            totals.put(source.name(), incomeRepository.sumAmountByUserIdAndSource(userId, source));
        }
        return totals;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getAllIncomes() {
        return incomeRepository.findAll().stream().map(IncomeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalIncomeCount() {
        return incomeRepository.count();
    }

    private Income findByIdAndUser(Long incomeId, Long userId) {
        return incomeRepository.findByIncomeIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Income %d not found for user %d", incomeId, userId)));
    }
}
