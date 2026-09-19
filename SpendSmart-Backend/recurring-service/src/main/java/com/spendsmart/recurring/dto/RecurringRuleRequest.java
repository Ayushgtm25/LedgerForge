package com.spendsmart.recurring.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecurringRuleRequest {
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(max = 150) private String title;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    private String currency = "USD";

    @NotBlank(message = "Type is required (EXPENSE or INCOME)")
    private String type;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 500) private String notes;
}
