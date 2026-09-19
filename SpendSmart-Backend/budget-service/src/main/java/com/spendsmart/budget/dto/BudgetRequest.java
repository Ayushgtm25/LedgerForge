package com.spendsmart.budget.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequest {

    private Long categoryId;

    @NotBlank(message = "Budget name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Budget limit is required")
    @DecimalMin(value = "0.01", message = "Budget limit must be positive")
    private BigDecimal budgetLimit;

    @Min(value = 1, message = "Alert threshold must be between 1 and 100")
    @Max(value = 100, message = "Alert threshold must be between 1 and 100")
    private Integer alertThreshold = 80;

    private String period = "MONTHLY";

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private String currency = "USD";
}
