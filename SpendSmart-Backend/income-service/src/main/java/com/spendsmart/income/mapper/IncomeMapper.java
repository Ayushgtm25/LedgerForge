package com.spendsmart.income.mapper;

import com.spendsmart.income.dto.IncomeResponse;
import com.spendsmart.income.entity.Income;

public final class IncomeMapper {

    private IncomeMapper() {}

    public static IncomeResponse toResponse(Income income) {
        return IncomeResponse.builder()
                .incomeId(income.getIncomeId())
                .userId(income.getUserId())
                .categoryId(income.getCategoryId())
                .title(income.getTitle())
                .amount(income.getAmount())
                .currency(income.getCurrency())
                .source(income.getSource().name())
                .date(income.getDate())
                .notes(income.getNotes())
                .isRecurring(income.getIsRecurring())
                .recurrencePeriod(income.getRecurrencePeriod() != null ? income.getRecurrencePeriod().name() : null)
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
