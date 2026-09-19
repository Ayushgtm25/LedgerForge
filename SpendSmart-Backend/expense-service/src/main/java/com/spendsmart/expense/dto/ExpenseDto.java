/**
 * ExpenseDto is a Data Transfer Object for expressing expense data to/from the API.
 *
 * This class is used in request/response payloads and decouples the entity model
 * from the API contract. It includes all relevant expense fields for frontend consumption.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDto {

    @JsonProperty("expenseId")
    private Long expenseId;

    @JsonProperty("categoryId")
    private Long categoryId;

    @NotBlank(message = "Title cannot be blank")
    @JsonProperty("title")
    private String title;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank")
    @JsonProperty("currency")
    private String currency;

    @NotNull(message = "Expense type cannot be null")
    @JsonProperty("type")
    private String type;

    @NotNull(message = "Payment method cannot be null")
    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @NotNull(message = "Date cannot be null")
    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("receiptUrl")
    private String receiptUrl;

    @JsonProperty("isRecurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}


