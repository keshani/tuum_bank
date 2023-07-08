package com.tuum.bank.modules.account.model;

import com.tuum.bank.common.models.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountBalance  implements Serializable {
    Long id;
    String accountId;
    CurrencyType currencyType;
    BigDecimal balance;
}
