package com.tuum.bank.exception.exceptionType;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidTransactionAmountException extends IllegalArgumentException {
    public InvalidTransactionAmountException(String message) {
        super(message);
    }
}
