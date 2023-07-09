
package com.tuum.bank.transaction.service;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import com.tuum.bank.exception.exceptionType.AccountNotFoundException;
import com.tuum.bank.exception.exceptionType.InsufficientBalanceException;
import com.tuum.bank.exception.exceptionType.InvalidTransactionAmountException;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.transaction.mapper.TransactionMapper;
import com.tuum.bank.modules.transaction.model.Transaction;
import com.tuum.bank.modules.transaction.model.TransactionDto;
import com.tuum.bank.modules.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountBalanceMapper accountBalanceMapper;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void init() {
    }

    @Test
    void test_credit_transaction() throws AccountNotFoundException {
        TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.IN,
                "transaction in", new BigDecimal(10));
        AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, BigDecimal.ZERO);

        Mockito.when(accountBalanceMapper.getBalanceByAccountIdAndCurrency(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(balance);
        Mockito.when(transactionMapper.saveTransaction(Mockito.any(Transaction.class))).thenReturn(1);
        Mockito.doNothing().when(accountBalanceMapper).updateAccountBalance(Mockito.any(Long.class), Mockito.any(BigDecimal.class));

        Transaction retriveTransaction = transactionService.saveTransaction(transactionDto);
        assertNotNull(retriveTransaction);
    }

    @Test
    void test_debit_transaction() throws AccountNotFoundException {
        TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.OUT,
                "transaction out", new BigDecimal(10));
        AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, new BigDecimal(100));

        Mockito.when(accountBalanceMapper.getBalanceByAccountIdAndCurrency(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(balance);
        Mockito.when(transactionMapper.saveTransaction(Mockito.any(Transaction.class))).thenReturn(1);
        Mockito.doNothing().when(accountBalanceMapper).updateAccountBalance(Mockito.any(Long.class), Mockito.any(BigDecimal.class));

        Transaction retriveTransaction = transactionService.saveTransaction(transactionDto);
        assertNotNull(retriveTransaction);
    }

    @Nested
    class test_transaction_amount{
        @Test
        void test_zero_transaction_amount() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.IN,
                    "transaction out", new BigDecimal(0));
            AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, BigDecimal.ZERO);
            assertThrows(InvalidTransactionAmountException.class,() ->transactionService.validateTransaction(transactionDto, balance));
        }

        @Test
        void test_minus_transactios_amount() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.IN,
                    "transaction out", new BigDecimal(-3));
            AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, BigDecimal.ZERO);
            assertThrows(InvalidTransactionAmountException.class,() ->transactionService.validateTransaction(transactionDto, balance));
        }

        @Test
        void test_transactios_balance_is_insufficient() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.OUT,
                    "transaction out", new BigDecimal(100));
            AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, BigDecimal.ZERO);
            assertThrows(InsufficientBalanceException.class,() ->transactionService.validateTransaction(transactionDto, balance));
        }
    }

    @Nested
    class test_transaction_balance{
        @Test
        void test_credit_transaction_balance() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.IN,
                    "transaction out", new BigDecimal(100));
            AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, new BigDecimal(10));
            BigDecimal balanceVal = transactionService.calculateRemainingBalance(transactionDto,balance);
            assertEquals(110,balanceVal.intValue());
        }

        @Test
        void test_debit_transaction_balance() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.OUT,
                    "transaction out", new BigDecimal(40));
            AccountBalance balance = new AccountBalance(1L,"123456",CurrencyType.EUR, new BigDecimal(100));
            BigDecimal balanceVal = transactionService.calculateRemainingBalance(transactionDto,balance);
            assertEquals(60,balanceVal.intValue());
        }
    }

}
