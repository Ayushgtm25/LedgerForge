package com.spendsmart.income.service;

import com.spendsmart.income.dto.IncomeRequest;
import com.spendsmart.income.dto.IncomeResponse;
import com.spendsmart.income.entity.Income.IncomeSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IncomeService {

    IncomeResponse createIncome(Long userId, IncomeRequest request);

    IncomeResponse updateIncome(Long userId, Long incomeId, IncomeRequest request);

    void deleteIncome(Long userId, Long incomeId);

    IncomeResponse getIncomeById(Long userId, Long incomeId);

    List<IncomeResponse> getIncomesByUser(Long userId);

    List<IncomeResponse> getIncomesBySource(Long userId, IncomeSource source);

    List<IncomeResponse> getIncomesByDateRange(Long userId, LocalDate from, LocalDate to);

    List<IncomeResponse> searchIncomes(Long userId, String keyword);

    BigDecimal getTotalIncome(Long userId);

    BigDecimal getTotalIncomeBySource(Long userId, IncomeSource source);

    BigDecimal getTotalIncomeByDateRange(Long userId, LocalDate from, LocalDate to);

    Map<String, BigDecimal> getIncomeTotals(Long userId);

    // Admin
    List<IncomeResponse> getAllIncomes();

    long getTotalIncomeCount();
}
