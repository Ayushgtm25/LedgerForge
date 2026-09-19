package com.spendsmart.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Snapshot entity storing periodic analytics summaries for a user.
 * Serves as a cache layer so dashboards don't need real-time aggregation every time.
 */
@Entity
@Table(
    name = "analytics_snapshots",
    indexes = {
        @Index(name = "idx_snapshot_user", columnList = "user_id"),
        @Index(name = "idx_snapshot_date", columnList = "snapshot_date"),
        @Index(name = "idx_snapshot_user_date", columnList = "user_id, snapshot_date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapshotId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "total_expenses", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    @Column(name = "total_income", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "net_savings", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal netSavings = BigDecimal.ZERO;

    @Column(name = "transaction_count")
    @Builder.Default
    private Integer transactionCount = 0;

    @Column(name = "top_expense_category", length = 100)
    private String topExpenseCategory;

    @Column(name = "top_income_source", length = 100)
    private String topIncomeSource;

    @Column(length = 10)
    @Builder.Default
    private String currency = "USD";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
