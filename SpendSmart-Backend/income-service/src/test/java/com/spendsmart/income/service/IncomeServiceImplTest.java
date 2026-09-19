package com.spendsmart.income.service;

import com.spendsmart.income.dto.IncomeRequest;
import com.spendsmart.income.dto.IncomeResponse;
import com.spendsmart.income.entity.Income;
import com.spendsmart.income.entity.Income.IncomeSource;
import com.spendsmart.income.exception.ResourceNotFoundException;
import com.spendsmart.income.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private static final Long USER_ID = 1L;
    private static final Long INCOME_ID = 10L;

    private Income sampleIncome;
    private IncomeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleIncome = Income.builder()
                .incomeId(INCOME_ID)
                .userId(USER_ID)
                .title("Monthly Salary")
                .amount(new BigDecimal("5000.00"))
                .currency("USD")
                .source(IncomeSource.SALARY)
                .date(LocalDate.of(2025, 1, 15))
                .isRecurring(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = IncomeRequest.builder()
                .title("Monthly Salary")
                .amount(new BigDecimal("5000.00"))
                .currency("USD")
                .source(IncomeSource.SALARY)
                .date(LocalDate.of(2025, 1, 15))
                .isRecurring(true)
                .build();
    }

    @Nested
    @DisplayName("Create Income")
    class CreateIncome {
        @Test
        @DisplayName("Should create income successfully")
        void shouldCreateIncome() {
            when(incomeRepository.save(any(Income.class))).thenReturn(sampleIncome);

            IncomeResponse result = incomeService.createIncome(USER_ID, sampleRequest);

            assertNotNull(result);
            assertEquals("Monthly Salary", result.getTitle());
            assertEquals(new BigDecimal("5000.00"), result.getAmount());
            verify(incomeRepository).save(any(Income.class));
        }
    }

    @Nested
    @DisplayName("Update Income")
    class UpdateIncome {
        @Test
        @DisplayName("Should update income successfully")
        void shouldUpdateIncome() {
            when(incomeRepository.findByIncomeIdAndUserId(INCOME_ID, USER_ID)).thenReturn(Optional.of(sampleIncome));
            when(incomeRepository.save(any(Income.class))).thenReturn(sampleIncome);

            IncomeResponse result = incomeService.updateIncome(USER_ID, INCOME_ID, sampleRequest);
            assertNotNull(result);
            verify(incomeRepository).save(any(Income.class));
        }

        @Test
        @DisplayName("Should reject update for non-existent income")
        void shouldRejectNonExistent() {
            when(incomeRepository.findByIncomeIdAndUserId(INCOME_ID, USER_ID)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> incomeService.updateIncome(USER_ID, INCOME_ID, sampleRequest));
        }
    }

    @Nested
    @DisplayName("Delete Income")
    class DeleteIncome {
        @Test
        @DisplayName("Should delete income successfully")
        void shouldDeleteIncome() {
            when(incomeRepository.findByIncomeIdAndUserId(INCOME_ID, USER_ID)).thenReturn(Optional.of(sampleIncome));
            incomeService.deleteIncome(USER_ID, INCOME_ID);
            verify(incomeRepository).delete(sampleIncome);
        }
    }

    @Nested
    @DisplayName("Get Incomes")
    class GetIncomes {
        @Test
        @DisplayName("Should get all incomes for user")
        void shouldGetAllIncomes() {
            when(incomeRepository.findByUserIdOrderByDateDesc(USER_ID)).thenReturn(List.of(sampleIncome));
            List<IncomeResponse> result = incomeService.getIncomesByUser(USER_ID);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should search incomes by keyword")
        void shouldSearchIncomes() {
            when(incomeRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByDateDesc(USER_ID, "Salary"))
                    .thenReturn(List.of(sampleIncome));
            List<IncomeResponse> result = incomeService.searchIncomes(USER_ID, "Salary");
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Income Totals")
    class IncomeTotals {
        @Test
        @DisplayName("Should get total income")
        void shouldGetTotal() {
            when(incomeRepository.sumAmountByUserId(USER_ID)).thenReturn(new BigDecimal("5000.00"));
            BigDecimal total = incomeService.getTotalIncome(USER_ID);
            assertEquals(new BigDecimal("5000.00"), total);
        }

        @Test
        @DisplayName("Should get income totals map")
        void shouldGetTotalsMap() {
            when(incomeRepository.sumAmountByUserId(USER_ID)).thenReturn(new BigDecimal("5000.00"));
            when(incomeRepository.sumAmountByUserIdAndSource(eq(USER_ID), any())).thenReturn(BigDecimal.ZERO);

            Map<String, BigDecimal> totals = incomeService.getIncomeTotals(USER_ID);
            assertNotNull(totals);
            assertEquals(new BigDecimal("5000.00"), totals.get("total"));
        }
    }
}
