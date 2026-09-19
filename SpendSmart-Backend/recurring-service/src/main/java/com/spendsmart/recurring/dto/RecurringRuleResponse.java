package com.spendsmart.recurring.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecurringRuleResponse {
    private Long ruleId;
    private Long userId;
    private Long categoryId;
    private String title;
    private BigDecimal amount;
    private String currency;
    private String type;
    private String frequency;
    private LocalDate startDate;
    private LocalDate nextDueDate;
    private LocalDate endDate;
    private String notes;
    private Boolean isActive;
    private boolean isDue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
