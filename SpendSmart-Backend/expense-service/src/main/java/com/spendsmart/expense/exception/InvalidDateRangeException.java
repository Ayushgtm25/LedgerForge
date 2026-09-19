/**
 * InvalidDateRangeException is thrown when date range parameters are invalid.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.exception;

public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException(String message) {
        super(message);
    }

    public InvalidDateRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}


