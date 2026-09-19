/**
 * BudgetSyncException is thrown when synchronization with the budget-service fails.
 *
 * This indicates a critical inter-service communication error that must be logged
 * and reported to the client.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.exception;

public class BudgetSyncException extends RuntimeException {

    public BudgetSyncException(String message) {
        super(message);
    }

    public BudgetSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}


