package com.tuum.bank.transaction.mapper;

import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import com.tuum.bank.modules.transaction.mapper.TransactionMapper;
import com.tuum.bank.modules.transaction.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MybatisTest
@Sql(scripts = "classpath:init/testdata.sql", config = @SqlConfig(separator = ";", commentPrefix = "--"))
public class TransactionMapperTest {

    @Autowired
    private TransactionMapper transactionMapper;

    @BeforeEach
    void init() {
    }

    @Test
    void test_save_transaction() {
        Transaction transaction = new Transaction("123456",CurrencyType.EUR, TransactionType.IN,
                "transaction in", new BigDecimal(10), new BigDecimal(10), LocalDateTime.now());
        int rowCount = transactionMapper.saveTransaction(transaction);
        assertEquals(1,rowCount);
    }

    @Test
    void test_save_transaction_with_invalid_accoutId() {
        Transaction transaction = new Transaction("21",CurrencyType.EUR, TransactionType.IN,
                "transaction in", new BigDecimal(10), new BigDecimal(10), LocalDateTime.now());
        assertThrows(RuntimeException.class,()->transactionMapper.saveTransaction(transaction));

    }

    @Nested
    class test_transaction_save_with_required_field{

        @Test
        void test_save_transaction_without_accoutId() {
            Transaction transaction = new Transaction(null,CurrencyType.EUR, TransactionType.IN,
                    "transaction in", new BigDecimal(10), new BigDecimal(10), LocalDateTime.now());
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }

        @Test
        void test_save_transaction_without_currencyType() {
            Transaction transaction = new Transaction("121212",null, TransactionType.IN,
                    "transaction in", new BigDecimal(10), new BigDecimal(10), LocalDateTime.now());
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }

        @Test
        void test_save_transaction_without_transactionType() {
            Transaction transaction = new Transaction("121212",CurrencyType.GBP, null,
                    "transaction in", new BigDecimal(10), new BigDecimal(10), LocalDateTime.now());
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }

        @Test
        void test_save_transaction_without_description() {
            Transaction transaction = new Transaction("121212",CurrencyType.GBP, TransactionType.IN,
                    null, new BigDecimal(10), new BigDecimal(10), LocalDateTime.now());
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }

        @Test
        void test_save_transaction_without_amount() {
            Transaction transaction = new Transaction("121212",CurrencyType.GBP, TransactionType.IN,
                    "transaction in", null, new BigDecimal(10), LocalDateTime.now());
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }

        @Test
        void test_save_transaction_without_balance() {
            Transaction transaction = new Transaction("121212",CurrencyType.GBP, TransactionType.IN,
                    "transaction in",  new BigDecimal(10), null, LocalDateTime.now());
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }

        @Test
        void test_save_transaction_without_transdatetime() {
            Transaction transaction = new Transaction("121212",CurrencyType.GBP, TransactionType.IN,
                    "transaction in",  new BigDecimal(10), new BigDecimal(10), null);
            assertThrows(RuntimeException.class,() ->transactionMapper.saveTransaction(transaction));
        }
    }

    @Test
    void test_get_transaction_by_valid_accountId() {
        List<Transaction> transactions = transactionMapper.getTransactionsByAccountId("123456");
        assertEquals(2,transactions.size());
    }

    @Test
    void test_get_transaction_by_invalid_accountId() {
        List<Transaction> transactions = transactionMapper.getTransactionsByAccountId("12356");
        assertEquals(0,transactions.size());
    }

}

