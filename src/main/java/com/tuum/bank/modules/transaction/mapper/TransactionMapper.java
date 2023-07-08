package com.tuum.bank.modules.transaction.mapper;

import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.transaction.model.Transaction;
import com.tuum.bank.modules.transaction.model.TransactionDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Insert("INSERT INTO transactions (account_id, currency_Type, transfer_amount," +
            " balance, transaction_type,transaction_desc, transaction_datetime)" +
            " VALUES ( #{accountId},#{currencyType}, #{transferAmount}, #{balance}," +
            " #{transactionType}, #{transDescription}, #{transactionDatetime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveTransaction(Transaction transaction);

    @Select("SELECT * FROM transactions WHERE account_id = #{accountId}")
    @Results({
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "currencyType", column = "currency_Type"),
            @Result(property = "transferAmount", column = "transfer_amount"),
            @Result(property = "balance", column = "balance"),
            @Result(property = "transactionType", column = "transaction_Type"),
            @Result(property = "transDescription", column = "transaction_desc")
     })
    List<Transaction> getTransactionsByAccountId(String accountId);

}