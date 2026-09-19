package com.spendsmart.income.dto;

import com.spendsmart.income.entity.Income.IncomeSource;
import com.spendsmart.income.entity.Income.RecurrencePeriod;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeRequest {

    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Size(max = 3, message = "Currency code must be 3 characters")
    private String currency;

    @NotNull(message = "Source is required")
    private IncomeSource source;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String notes;

    private Boolean isRecurring;

    private RecurrencePeriod recurrencePeriod;
}
