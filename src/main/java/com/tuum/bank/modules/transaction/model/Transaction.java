package com.tuum.bank.modules.transaction.model;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction implements Serializable {
    Long id;
    String accountId;
    CurrencyType currencyType;
    TransactionType transactionType;
    String transDescription;
    BigDecimal transferAmount;
    BigDecimal balance;
    LocalDateTime transactionDatetime;

    public Transaction(String accountId, CurrencyType currencyType, TransactionType transactionType, String transDescription, BigDecimal transferAmount, BigDecimal balance, LocalDateTime transactionDatetime) {
        this.accountId = accountId;
        this.currencyType = currencyType;
        this.transactionType = transactionType;
        this.transDescription = transDescription;
        this.transferAmount = transferAmount;
        this.balance = balance;
        this.transactionDatetime = transactionDatetime;
    }
}
