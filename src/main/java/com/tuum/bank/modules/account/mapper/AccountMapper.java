package com.tuum.bank.modules.account.mapper;

import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.account.model.AccountDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccountMapper {

    @Insert("INSERT INTO account (account_id, customer_id, country )" +
            " VALUES ( #{accountId},#{customerId}, #{country})")
    void insertAccount(AccountDto account);

    @Select("SELECT * FROM account WHERE account_id = #{accountId}")
    @Results({
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "country", column = "country"),
            @Result(property = "accountBalanceList", column = "account_id", javaType = List.class,
                    many = @Many(select = "com.tuum.bank.modules.account.mapper.AccountBalanceMapper.getAccountBalancesByAccountId"))
    })
    Account getAccountWithBalances(String accountId);

    @Select("SELECT * FROM account WHERE account_id = #{accountId}")
    @Results({
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "country", column = "country") })
    Account getAccount(String accountId);




}
