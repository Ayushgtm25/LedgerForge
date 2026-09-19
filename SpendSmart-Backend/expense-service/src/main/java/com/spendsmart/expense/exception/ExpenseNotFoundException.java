/**
 * ExpenseNotFoundException is thrown when an expense record cannot be found.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(String message) {
        super(message);
    }

    public ExpenseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpenseNotFoundException(Long expenseId) {
        super("Expense with ID " + expenseId + " not found");
    }
}


