package com.tuum.bank.modules.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account implements Serializable {
    String accountId ;
    String customerId;
    String country;
    List<AccountBalance> accountBalanceList;

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", country='" + country + '\'' +
                ", accountBalanceList=" + accountBalanceList +
                '}';
    }
}
