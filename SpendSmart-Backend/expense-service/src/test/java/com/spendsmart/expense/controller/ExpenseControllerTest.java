package com.spendsmart.expense.controller;
import com.spendsmart.expense.dto.ExpenseDto;
import com.spendsmart.expense.security.JwtUserDetails;
import com.spendsmart.expense.service.ExpenseService;
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
class ExpenseControllerTest {
    @Mock private ExpenseService service;
    @InjectMocks private ExpenseController controller;

    @Test
    void testGetExpenses() {
        JwtUserDetails user = new JwtUserDetails(1L, "test@test.com", "USER", "PAID");
        org.springframework.security.core.Authentication auth = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        when(auth.getDetails()).thenReturn(user);
        when(service.getExpensesByUser(1L)).thenReturn(List.of(ExpenseDto.builder().build()));
        ResponseEntity<List<ExpenseDto>> res = controller.getExpenses(auth, null, null, null, null, null, null, null, null);
        assertEquals(200, res.getStatusCode().value());
    }
}
