/**
 * UnauthorizedAccessException is thrown when a user attempts to access or modify
 * another user's expense. This protects data isolation.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}


