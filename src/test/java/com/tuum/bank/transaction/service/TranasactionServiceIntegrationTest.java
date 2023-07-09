package com.tuum.bank.transaction.service;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import com.tuum.bank.exception.exceptionType.AccountNotFoundException;
import com.tuum.bank.exception.exceptionType.InsufficientBalanceException;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.transaction.mapper.TransactionMapper;
import com.tuum.bank.modules.transaction.model.Transaction;
import com.tuum.bank.modules.transaction.model.TransactionDto;
import com.tuum.bank.modules.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@AutoConfigureMybatis
@SpringBootTest
@Rollback
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:init/testdata.sql", config = @SqlConfig(separator = ";", commentPrefix = "--")
        , executionPhase = BEFORE_TEST_METHOD)
class TranasactionServiceIntegrationTest {

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void init() {
    }

    @Test
    void test_save_transaction_with_invalid_accountId() throws AccountNotFoundException {
        TransactionDto transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                "transaction out", new BigDecimal(10));
        assertThrows(AccountNotFoundException.class, () -> transactionService.saveTransaction(transactionDto));
    }

    @Test
    void test_save_transaction_valid_accountId_with_invalid_currency() throws AccountNotFoundException {
        TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.GBP, TransactionType.IN,
                "transaction out", new BigDecimal(10));
        assertThrows(AccountNotFoundException.class, () -> transactionService.saveTransaction(transactionDto));
    }

    @Test
    void test_save_transaction_with_insufficient_balance() throws AccountNotFoundException {
        TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.OUT,
                "transaction out", new BigDecimal(110));
        assertThrows(InsufficientBalanceException.class, () -> transactionService.saveTransaction(transactionDto));
    }

    @Nested
    class test_transaction_with_update_balance{
        @Test
        @Transactional
        void test_credit_transactios() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.IN,
                    "transaction out", new BigDecimal(100));
            transactionService.saveTransaction(transactionDto);
           AccountBalance balance = accountBalanceMapper.getBalanceByAccountIdAndCurrency("123456",CurrencyType.EUR.toString());
           assertEquals(200,balance.getBalance().intValue());
        }

        @Test
        @Transactional
        void test_debit_transactios() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.OUT,
                    "transaction out", new BigDecimal(50));
            transactionService.saveTransaction(transactionDto);
            AccountBalance balance = accountBalanceMapper.getBalanceByAccountIdAndCurrency("123456",CurrencyType.EUR.toString());
            assertEquals(50,balance.getBalance().intValue());
        }

    }

    @Test
    void test_get_transaction_with_valid_accountId() throws AccountNotFoundException {
          List<Transaction> transList = transactionService.getTransactionsByAccountId("123456");
          assertEquals(2, transList.size());
    }

    @Test
    void test_get_transaction_with_invalid_accountId() throws AccountNotFoundException {
        assertThrows(AccountNotFoundException.class, ()-> transactionService.getTransactionsByAccountId("13456"));
    }

}
