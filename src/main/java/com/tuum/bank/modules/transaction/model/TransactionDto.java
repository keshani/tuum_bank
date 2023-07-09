package com.tuum.bank.modules.transaction.model;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    @NotNull(message = "Account Id is required")
    @NotEmpty(message = "Account Id cannot be empty")
    String accountId;

    @NotNull(message = "CurrencyType is required")
    CurrencyType currencyType;

    @NotNull(message = "TransactionType is required")
    TransactionType transactionType;

    @NotNull(message = "Description is required")
    @NotEmpty(message = "Description cannot be empty")
    String transDescription;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be non-negative")
    BigDecimal amount;
}
