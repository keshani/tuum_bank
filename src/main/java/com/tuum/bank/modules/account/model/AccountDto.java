package com.tuum.bank.modules.account.model;

import com.tuum.bank.common.models.CurrencyType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDto {
    String accountId ;

    @NotNull(message = "Customer Id cannot be null")
    @NotEmpty(message = "Customer Id cannot be empty")
    String customerId;

    @NotNull(message = "Country cannot be null")
    @NotEmpty(message = "Country cannot be empty")
    String country;

    @NotNull(message = "Currency Types cannot be empty")
    List<CurrencyType> currencyTypeList;
}
