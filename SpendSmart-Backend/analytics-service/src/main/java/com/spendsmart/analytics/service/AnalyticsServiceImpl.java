package com.spendsmart.analytics.service;

import com.spendsmart.analytics.dto.AnalyticsSummaryResponse;
import com.spendsmart.analytics.dto.MonthlyTrend;
import com.spendsmart.analytics.entity.AnalyticsSnapshot;
import com.spendsmart.analytics.repository.AnalyticsSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl {

    private final AnalyticsSnapshotRepository snapshotRepository;

    /**
     * Get the latest analytics summary for a user.
     */
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getLatestSummary(Long userId) {
        log.info("Fetching latest analytics summary for user {}", userId);

        return snapshotRepository.findTopByUserIdOrderBySnapshotDateDesc(userId)
                .map(this::toSummary)
                .orElseGet(() -> AnalyticsSummaryResponse.builder()
                        .userId(userId)
                        .totalExpenses(BigDecimal.ZERO)
                        .totalIncome(BigDecimal.ZERO)
                        .netSavings(BigDecimal.ZERO)
                        .transactionCount(0)
                        .savingsRate(0.0)
                        .currency("USD")
                        .build());
    }

    /**
     * Get analytics snapshots for a date range.
     */
    @Transactional(readOnly = true)
    public List<AnalyticsSummaryResponse> getSummariesByDateRange(Long userId, LocalDate from, LocalDate to) {
        log.info("Fetching analytics for user {} from {} to {}", userId, from, to);
        return snapshotRepository.findByUserIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(userId, from, to)
                .stream().map(this::toSummary).toList();
    }

    /**
     * Get monthly trends for the specified number of months.
     */
    @Transactional(readOnly = true)
    public List<MonthlyTrend> getMonthlyTrends(Long userId, int months) {
        log.info("Fetching {} months of trends for user {}", months, userId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months).withDayOfMonth(1);

        List<AnalyticsSnapshot> snapshots = snapshotRepository
                .findByUserIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(userId, startDate, endDate);

        return snapshots.stream().map(s -> MonthlyTrend.builder()
                .year(s.getSnapshotDate().getYear())
                .month(s.getSnapshotDate().getMonthValue())
                .monthName(Month.of(s.getSnapshotDate().getMonthValue())
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .totalExpenses(s.getTotalExpenses())
                .totalIncome(s.getTotalIncome())
                .netSavings(s.getNetSavings())
                .transactionCount(s.getTransactionCount())
                .build()
        ).toList();
    }

    /**
     * Save or update an analytics snapshot for a specific date.
     */
    public AnalyticsSummaryResponse saveSnapshot(Long userId, LocalDate date,
                                                  BigDecimal totalExpenses, BigDecimal totalIncome,
                                                  int transactionCount, String topExpenseCategory,
                                                  String topIncomeSource) {
        log.info("Saving analytics snapshot for user {} on {}", userId, date);

        AnalyticsSnapshot snapshot = snapshotRepository.findByUserIdAndSnapshotDate(userId, date)
                .orElse(AnalyticsSnapshot.builder()
                        .userId(userId)
                        .snapshotDate(date)
                        .build());

        snapshot.setTotalExpenses(totalExpenses);
        snapshot.setTotalIncome(totalIncome);
        snapshot.setNetSavings(totalIncome.subtract(totalExpenses));
        snapshot.setTransactionCount(transactionCount);
        snapshot.setTopExpenseCategory(topExpenseCategory);
        snapshot.setTopIncomeSource(topIncomeSource);

        snapshot = snapshotRepository.save(snapshot);
        log.info("Analytics snapshot saved: id={}", snapshot.getSnapshotId());
        return toSummary(snapshot);
    }

    /**
     * Get all snapshots for a user (ordered most recent first).
     */
    @Transactional(readOnly = true)
    public List<AnalyticsSummaryResponse> getAllSnapshots(Long userId) {
        return snapshotRepository.findByUserIdOrderBySnapshotDateDesc(userId)
                .stream().map(this::toSummary).toList();
    }

    public String exportDataAsCsv(Long userId) {
        List<AnalyticsSnapshot> data = snapshotRepository.findByUserIdOrderBySnapshotDateDesc(userId);
        StringBuilder sb = new StringBuilder("Date,TotalIncome,TotalExpense,NetSavings,Transactions\n");
        for (AnalyticsSnapshot s : data) {
            sb.append(s.getSnapshotDate()).append(",")
              .append(s.getTotalIncome()).append(",")
              .append(s.getTotalExpenses()).append(",")
              .append(s.getNetSavings()).append(",")
              .append(s.getTransactionCount()).append("\n");
        }
        return sb.toString();
    }

    public byte[] exportDataAsPdf(Long userId) {
        // Dummy PDF export
        return "PDF Data".getBytes();
    }

    private AnalyticsSummaryResponse toSummary(AnalyticsSnapshot snapshot) {
        double savingsRate = 0.0;
        if (snapshot.getTotalIncome().compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = snapshot.getNetSavings()
                    .divide(snapshot.getTotalIncome(), 4, RoundingMode.HALF_UP)
                    .doubleValue() * 100.0;
        }

        return AnalyticsSummaryResponse.builder()
                .userId(snapshot.getUserId())
                .periodStart(snapshot.getSnapshotDate().withDayOfMonth(1))
                .periodEnd(snapshot.getSnapshotDate())
                .totalExpenses(snapshot.getTotalExpenses())
                .totalIncome(snapshot.getTotalIncome())
                .netSavings(snapshot.getNetSavings())
                .transactionCount(snapshot.getTransactionCount())
                .topExpenseCategory(snapshot.getTopExpenseCategory())
                .topIncomeSource(snapshot.getTopIncomeSource())
                .savingsRate(savingsRate)
                .currency(snapshot.getCurrency())
                .build();
    }
}
