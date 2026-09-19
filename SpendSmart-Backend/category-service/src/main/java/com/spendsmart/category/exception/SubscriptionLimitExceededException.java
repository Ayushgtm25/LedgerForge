package com.spendsmart.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SubscriptionLimitExceededException extends RuntimeException {
    public SubscriptionLimitExceededException(String message) {
        super(message);
    }
}

