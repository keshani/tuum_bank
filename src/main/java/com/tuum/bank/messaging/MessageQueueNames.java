package com.tuum.bank.messaging;

import org.springframework.stereotype.Component;

@Component
public interface MessageQueueNames {
    public static final String ACCOUNT_CREATION = "ACCOUNT_CREATION";
    public static final String CREDIT_TRANSACTION = "CREDIT_TRANSACTION";
    public static final String DEBIT_TRANSACTION = "DEBIT_TRANSACTION";
}
