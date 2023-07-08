package com.tuum.bank.modules.account.mapper;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.modules.account.model.AccountBalance;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountBalanceMapper {
    @Insert("INSERT INTO account_balance (account_id, currency_Type) VALUES ( #{accountId},#{currencyType})")
    void insertAccountBalance(AccountBalance accountBalance);

    @Update("UPDATE account_balance SET balance = #{balance} WHERE id = #{id}")
    void updateAccountBalance(Long id, BigDecimal balance);

    @Select("SELECT * FROM account_balance WHERE account_id = #{accountId}")
    @Results({
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "id", column = "id"),
            @Result(property = "currencyType", column = "currency_Type"),
            @Result(property = "balance", column = "balance")
    })
    List<AccountBalance> getAccountBalancesByAccountId(String accountId);

    @Select("SELECT * FROM account_balance WHERE account_id = #{accountId} " +
            "AND currency_Type = #{currencyType}" )
    @Results({
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "id", column = "id"),
            @Result(property = "currencyType", column = "currency_Type"),
            @Result(property = "balance", column = "balance")
    })
    AccountBalance getBalanceByAccountIdAndCurrency(String accountId, String currencyType);
}