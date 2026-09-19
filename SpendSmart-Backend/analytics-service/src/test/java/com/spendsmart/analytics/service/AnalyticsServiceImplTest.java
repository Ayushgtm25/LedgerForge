package com.spendsmart.analytics.service;
import com.spendsmart.analytics.dto.AnalyticsSummaryResponse;
import com.spendsmart.analytics.entity.AnalyticsSnapshot;
import com.spendsmart.analytics.repository.AnalyticsSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock private AnalyticsSnapshotRepository repository;
    @InjectMocks private AnalyticsServiceImpl service;

    @Test
    void testGetLatestSummary() {
        AnalyticsSnapshot s = AnalyticsSnapshot.builder().userId(1L).totalIncome(BigDecimal.TEN).totalExpenses(BigDecimal.ONE).netSavings(BigDecimal.ONE).snapshotDate(LocalDate.now()).build();
        when(repository.findTopByUserIdOrderBySnapshotDateDesc(1L)).thenReturn(Optional.of(s));
        AnalyticsSummaryResponse res = service.getLatestSummary(1L);
        assertNotNull(res);
    }

    @Test
    void testExportDataAsCsv() {
        AnalyticsSnapshot s = AnalyticsSnapshot.builder().userId(1L).totalIncome(BigDecimal.TEN).totalExpenses(BigDecimal.ONE).netSavings(BigDecimal.ONE).snapshotDate(LocalDate.now()).build();
        when(repository.findByUserIdOrderBySnapshotDateDesc(1L)).thenReturn(List.of(s));
        String res = service.exportDataAsCsv(1L);
        assertNotNull(res);
    }
}
