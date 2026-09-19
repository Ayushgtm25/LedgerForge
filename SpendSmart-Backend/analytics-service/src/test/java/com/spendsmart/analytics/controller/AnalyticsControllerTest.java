package com.spendsmart.analytics.controller;
import com.spendsmart.analytics.dto.AnalyticsSummaryResponse;
import com.spendsmart.analytics.security.JwtUserDetails;
import com.spendsmart.analytics.service.AnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {
    @Mock private AnalyticsServiceImpl service;
    @InjectMocks private AnalyticsController controller;

    @Test
    void testGetLatestSummary() {
        JwtUserDetails user = new JwtUserDetails(1L, "test@test.com", "USER", "PAID");
        when(service.getLatestSummary(1L)).thenReturn(AnalyticsSummaryResponse.builder().build());
        ResponseEntity<AnalyticsSummaryResponse> res = controller.getLatestSummary(user);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testExportCsv() {
        JwtUserDetails user = new JwtUserDetails(1L, "test@test.com", "USER", "PAID");
        when(service.exportDataAsCsv(1L)).thenReturn("data");
        ResponseEntity<String> res = controller.exportCsv(user);
        assertEquals(200, res.getStatusCode().value());
        
        JwtUserDetails user2 = new JwtUserDetails(1L, "test@test.com", "USER", "NORMAL");
        ResponseEntity<String> res2 = controller.exportCsv(user2);
        assertEquals(403, res2.getStatusCode().value());
    }
}
