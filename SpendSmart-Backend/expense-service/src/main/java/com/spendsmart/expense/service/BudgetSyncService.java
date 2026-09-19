/**
 * BudgetSyncService handles synchronous inter-service communication with budget-service.
 *
 * When expenses are created, updated (if amount/category changes), or deleted,
 * this service makes a synchronous RestTemplate call to budget-service to
 * atomically update the budget's spent amount.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.service;

import com.spendsmart.expense.dto.BudgetSyncRequest;
import com.spendsmart.expense.exception.BudgetSyncException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@Slf4j
public class BudgetSyncService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.budget-service.url}")
    private String budgetServiceUrl;

    @Value("${services.budget-service.endpoints.update-spent-amount}")
    private String updateSpentAmountEndpoint;

    /**
     * Synchronize expense amount with budget-service.
     *
     * This method sends a request to the budget-service to update the
     * budget's spent amount. A positive deltaAmount increments spent,
     * a negative deltaAmount decrements spent.
     *
     * @param userId the user's ID
     * @param categoryId the category ID (can be null for aggregate updates)
     * @param deltaAmount the change in amount (positive or negative)
     * @param currency the currency code
     * @throws BudgetSyncException if sync fails
     */
    public void syncBudgetSpentAmount(Long userId, Long categoryId, BigDecimal deltaAmount, String currency) {
        try {
            // Build the request
            BudgetSyncRequest request = BudgetSyncRequest.builder()
                    .userId(userId)
                    .categoryId(categoryId)
                    .deltaAmount(deltaAmount)
                    .currency(currency)
                    .build();

            String url = budgetServiceUrl + updateSpentAmountEndpoint;

            log.debug("Syncing budget for userId: {}, categoryId: {}, deltaAmount: {}",
                    userId, categoryId, deltaAmount);

            // Make synchronous call to budget-service
            HttpEntity<BudgetSyncRequest> requestEntity = new HttpEntity<>(request);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            // Verify success
            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new BudgetSyncException("Budget service returned status: " + response.getStatusCode());
            }

            log.info("Budget sync successful for userId: {}, delta: {}", userId, deltaAmount);

        } catch (RestClientException ex) {
            log.error("Budget service communication failed: {}", ex.getMessage(), ex);
            throw new BudgetSyncException("Failed to sync with budget-service: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error during budget sync: {}", ex.getMessage(), ex);
            throw new BudgetSyncException("Budget synchronization failed: " + ex.getMessage(), ex);
        }
    }
}


