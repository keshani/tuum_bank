package com.tuum.bank.exception.exceptionType;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message) {
        super(message);
    }
}
