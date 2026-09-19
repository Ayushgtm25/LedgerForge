/**
 * UpdateExpenseDto is used for PATCH/PUT requests to update expense information.
 *
 * Fields are optional to support partial updates. Only non-null fields are updated.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateExpenseDto {

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("title")
    private String title;

    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("type")
    private String type;

    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("isRecurring")
    private Boolean isRecurring;
}


