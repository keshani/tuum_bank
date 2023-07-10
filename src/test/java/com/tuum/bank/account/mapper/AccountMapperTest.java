package com.tuum.bank.account.mapper;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.mapper.AccountMapper;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.account.model.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@Sql(scripts = "classpath:init/testdata.sql", config = @SqlConfig(separator = ";", commentPrefix = "--"))
public class AccountMapperTest {

    @Autowired
    private AccountMapper mapper;

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @BeforeEach
    void init() {
    }

    @Test
    @Transactional
    void test_save_account() {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP);
        AccountDto accountDto =  new AccountDto("123","testcustomer1", "Estonia", currencyType);
        int rowCount = mapper.insertAccount(accountDto);
        assertEquals(1,rowCount);
     }

    @Test
    @Transactional
    void test_save_account_with_exsiting_accountId() {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP);
        AccountDto accountDto =  new AccountDto("123456","testcustomer1", "Estonia", currencyType);
        assertThrows(RuntimeException.class,() -> mapper.insertAccount(accountDto));
    }

    @Test
    void test_get_account_by_accountid() {
        Account account = mapper.getAccount("123456");
        assertAll( () -> assertEquals("123456", account.getAccountId()),
                () -> assertEquals("TestTuumCustomer", account.getCustomerId()),
                () -> assertEquals("Estonia", account.getCountry()));
    }

    @Test
    @Transactional
    void test_save_accountbalance() {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP);
        AccountDto accountDto =  new AccountDto("123","testcustomer1", "Estonia", currencyType);
        mapper.insertAccount(accountDto);

        AccountBalance accBalance = new AccountBalance();
        accBalance.setAccountId("123");
        accBalance.setCurrencyType(CurrencyType.EUR);
        int rawCount = accountBalanceMapper.insertAccountBalance(accBalance);

        assertEquals(1,rawCount);
    }

    @Test
    void test_get_accountbalance_by_accountid() {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR);
        Account account = mapper.getAccountWithBalances("123456");
        assertAll( () -> assertEquals("123456", account.getAccountId()),
                () -> assertEquals("TestTuumCustomer", account.getCustomerId()),
                () -> assertEquals("Estonia",account.getCountry()),
        () -> assertEquals(1, account.getAccountBalanceList().size()));

        List<CurrencyType> resultCurrencyType = account.getAccountBalanceList().stream()
                .map(AccountBalance::getCurrencyType).collect(Collectors.toList());
        assertIterableEquals(currencyType, resultCurrencyType);
    }
}
