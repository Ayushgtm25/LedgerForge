package com.spendsmart.budget.controller;
import com.spendsmart.budget.dto.BudgetResponse;
import com.spendsmart.budget.security.JwtUserDetails;
import com.spendsmart.budget.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {
    @Mock private BudgetService service;
    @InjectMocks private BudgetController controller;

    @Test
    void testGetBudgets() {
        JwtUserDetails user = new JwtUserDetails(1L, "test@test.com", "USER", "PAID");
        when(service.getBudgetsByUser(1L)).thenReturn(List.of(BudgetResponse.builder().build()));
        ResponseEntity<List<BudgetResponse>> res = controller.getBudgets(user);
        assertEquals(200, res.getStatusCode().value());
    }
}
