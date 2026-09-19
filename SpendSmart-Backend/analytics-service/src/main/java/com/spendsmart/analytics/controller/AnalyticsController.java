package com.spendsmart.analytics.controller;

import com.spendsmart.analytics.dto.AnalyticsSummaryResponse;
import com.spendsmart.analytics.dto.MonthlyTrend;
import com.spendsmart.analytics.security.JwtUserDetails;
import com.spendsmart.analytics.service.AnalyticsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
public class AnalyticsController {

    private final AnalyticsServiceImpl analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get latest analytics summary")
    public ResponseEntity<AnalyticsSummaryResponse> getLatestSummary(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(analyticsService.getLatestSummary(user.getUserId()));
    }

    @GetMapping("/summary/range")
    @Operation(summary = "Get analytics summaries for a date range")
    public ResponseEntity<List<AnalyticsSummaryResponse>> getSummariesByRange(
            @AuthenticationPrincipal JwtUserDetails user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsService.getSummariesByDateRange(user.getUserId(), from, to));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get monthly trends for the last N months")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrends(
            @AuthenticationPrincipal JwtUserDetails user,
            @RequestParam(defaultValue = "12") int months) {
        return ResponseEntity.ok(analyticsService.getMonthlyTrends(user.getUserId(), months));
    }

    @GetMapping("/history")
    @Operation(summary = "Get all analytics snapshots")
    public ResponseEntity<List<AnalyticsSummaryResponse>> getHistory(
            @AuthenticationPrincipal JwtUserDetails user) {
        return ResponseEntity.ok(analyticsService.getAllSnapshots(user.getUserId()));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export analytics data as CSV (Paid users only)")
    public ResponseEntity<String> exportCsv(@AuthenticationPrincipal JwtUserDetails user) {
        if (!"PAID".equalsIgnoreCase(user.getSubscriptionType())) {
            return ResponseEntity.status(403).body("Export is only available for PAID users.");
        }
        return ResponseEntity.ok(analyticsService.exportDataAsCsv(user.getUserId()));
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Export analytics data as PDF (Paid users only)")
    public ResponseEntity<byte[]> exportPdf(@AuthenticationPrincipal JwtUserDetails user) {
        if (!"PAID".equalsIgnoreCase(user.getSubscriptionType())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(analyticsService.exportDataAsPdf(user.getUserId()));
    }
}
