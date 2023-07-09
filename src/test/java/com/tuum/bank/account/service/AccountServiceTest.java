package com.tuum.bank.account.service;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.mapper.AccountMapper;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.account.model.AccountDto;
import com.tuum.bank.modules.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountBalanceMapper accountBalanceMapper;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void init() {
    }

    @Test
    void test_save_account() {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP);
        AccountDto accountDto =  new AccountDto("testcustomer1", "Estonia", currencyType);
        List<AccountBalance> balanceList = List.of(new AccountBalance(1L,"123",CurrencyType.EUR, BigDecimal.ZERO),
                                                   new AccountBalance(2L,"124",CurrencyType.GBP, BigDecimal.ZERO));
        Account account = new Account("123","testcustomer1", "Estonia",balanceList );
        Mockito.when(accountMapper.insertAccount(Mockito.any(AccountDto.class))).thenReturn(1);
        Mockito.when(accountBalanceMapper.insertAccountBalance(Mockito.any(AccountBalance.class))).thenReturn(1);
        Mockito.when(accountMapper.getAccountWithBalances(Mockito.anyString())).thenReturn(account);

        Account retriveAccount = accountService.saveAccount(accountDto);

        assertNotNull(retriveAccount);
        assertEquals(account.getAccountBalanceList().size(), retriveAccount.getAccountBalanceList().size());
        assertEquals(account.getCustomerId(), retriveAccount.getCustomerId());
    }

    @Test
    void test_get_account_by_id() {
        List<AccountBalance> balanceList = List.of(new AccountBalance(1L,"123",CurrencyType.EUR, BigDecimal.ZERO),
                new AccountBalance(2L,"124",CurrencyType.GBP, BigDecimal.ZERO));
        Account account = new Account("123","testcustomer1", "Estonia",balanceList );;
        Mockito.when(accountMapper.getAccountWithBalances(Mockito.anyString())).thenReturn(account);

        Account retriveAccount = accountService.getAccountById("123");

        assertNotNull(retriveAccount);
        assertEquals(account.getAccountBalanceList().size(), retriveAccount.getAccountBalanceList().size());
        assertEquals(account.getCustomerId(), retriveAccount.getCustomerId());
    }
}