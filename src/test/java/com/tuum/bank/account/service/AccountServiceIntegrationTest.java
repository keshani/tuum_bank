package com.tuum.bank.account.service;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import com.tuum.bank.exception.exceptionType.AccountNotFoundException;
import com.tuum.bank.exception.exceptionType.InsufficientBalanceException;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.mapper.AccountMapper;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.account.model.AccountDto;
import com.tuum.bank.modules.account.service.AccountService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@AutoConfigureMybatis
@SpringBootTest
@Rollback
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:init/testdata.sql", config = @SqlConfig(separator = ";", commentPrefix = "--")
        , executionPhase = BEFORE_TEST_METHOD)
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void init() {
    }

    @Test
    @Transactional
    void test_save_account() throws AccountNotFoundException {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP, CurrencyType.SEK, CurrencyType.USD);
        AccountDto accountDto =  new AccountDto("testTuumCustomer", "Estonia", currencyType);
        Account retriveAccount = accountService.saveAccount(accountDto);
        assertNotNull(retriveAccount.getAccountId());
        assertEquals(4, retriveAccount.getAccountBalanceList().size());
    }

    @Test
    void test_get_account_with_invalid_accountId() throws AccountNotFoundException {
         Account account = accountService.getAccountById("13456");
         assertNull(account);
    }

}
