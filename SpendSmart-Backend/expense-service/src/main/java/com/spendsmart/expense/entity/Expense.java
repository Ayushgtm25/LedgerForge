/**
 * Expense entity class representing a single expense transaction.
 *
 * This class models the core data structure for an expense in SpendSmart.
 * Each expense is strictly scoped to a user (userId), ensuring data isolation.
 * Expenses can be categorized, support multiple payment methods, and can have
 * optional receipt attachments for audit purposes.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_date", columnList = "user_id,date"),
        @Index(name = "idx_user_category", columnList = "user_id,category_id"),
        @Index(name = "idx_user_type", columnList = "user_id,type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    /**
     * Unique identifier for the expense. Auto-increment primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    /**
     * User ID that owns this expense. CRITICAL: Must be used for all queries to enforce data isolation.
     */
    @NotNull(message = "User ID cannot be null")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Category ID for classification (links to category service).
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Expense title or description.
     */
    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * Amount of the expense. Stored as BigDecimal for precision.
     */
    @NotNull(message = "Amount cannot be null")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code (e.g., USD, INR, EUR).
     */
    @NotBlank(message = "Currency cannot be blank")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Type of expense (EXPENSE or SPLIT).
     */
    @NotNull(message = "Expense type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ExpenseType type;

    /**
     * Payment method used (CASH, CARD, UPI, BANK_TRANSFER, WALLET).
     */
    @NotNull(message = "Payment method cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * Date of the expense.
     */
    @NotNull(message = "Date cannot be null")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Optional notes or description for the expense.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * URL or reference to the receipt (stored in S3).
     */
    @Column(name = "receipt_url", length = 2048)
    private String receiptUrl;

    /**
     * Flag indicating if this is a recurring expense.
     */
    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    /**
     * Timestamp when this expense was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this expense was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for expense types.
     */
    public enum ExpenseType {
        EXPENSE,
        SPLIT
    }

    /**
     * Enum for payment methods.
     */
    public enum PaymentMethod {
        CASH,
        CARD,
        UPI,
        BANK_TRANSFER,
        WALLET
    }
}


