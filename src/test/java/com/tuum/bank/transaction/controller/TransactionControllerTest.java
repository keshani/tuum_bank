package com.tuum.bank.transaction.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.bank.CommonUtilFunction;
import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.common.models.TransactionType;
import com.tuum.bank.modules.transaction.mapper.TransactionMapper;
import com.tuum.bank.modules.transaction.model.Transaction;
import com.tuum.bank.modules.transaction.model.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@WebAppConfiguration
@AutoConfigureMybatis
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:init/testdata.sql", config = @SqlConfig(separator = ";", commentPrefix = "--")
        , executionPhase = BEFORE_TEST_METHOD)
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TransactionMapper transactionMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    public void test_create_transaction_with_valid_data() throws Exception {

        TransactionDto transactionDto = new TransactionDto("123456",CurrencyType.EUR, TransactionType.IN,
                                         "transaction in", new BigDecimal(10));
        MvcResult result = mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CommonUtilFunction.asJsonString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty()).andReturn();
        String content = result.getResponse().getContentAsString();

        Transaction transaction = new ObjectMapper().findAndRegisterModules().readValue(content, Transaction.class);

        assertAll(
                ()-> assertEquals("123456", transaction.getAccountId()),
                ()-> assertEquals(110, transaction.getBalance().intValue()),
                ()-> assertEquals( TransactionType.IN, transaction.getTransactionType())
        );
    }

    @Nested
    class test_transaction_save_with_invalid_data{

        @Test
        void test_save_transaction_without_accoutId() throws Exception {
            TransactionDto transactionDto = new TransactionDto(null,CurrencyType.EUR, TransactionType.IN,
                    "transaction in", new BigDecimal(10));
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());

            transactionDto = new TransactionDto("",CurrencyType.EUR, TransactionType.IN,
                    "transaction in", new BigDecimal(10));
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void test_save_transaction_without_currencyType() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123",null, TransactionType.IN,
                    "transaction in", new BigDecimal(10));

            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());

            transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                    "transaction in", new BigDecimal(10));
            String dto = CommonUtilFunction.asJsonString(transactionDto).replace("EUR","EUWWW");
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void test_save_transaction_without_transactionType() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123",CurrencyType.EUR, null,
                    "transaction in", new BigDecimal(10));

            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());

            transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.OUT,
                    "transaction in", new BigDecimal(10));
            String dto = CommonUtilFunction.asJsonString(transactionDto).replace("OUT","DEBIT");
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void test_save_transaction_without_description() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                    null, new BigDecimal(10));
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());

            transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                    "", new BigDecimal(10));
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void test_save_transaction_without_amount() throws Exception {
            TransactionDto transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                    "transaction", null);
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());
            transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                    "transaction", BigDecimal.ZERO);
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());

            transactionDto = new TransactionDto("123",CurrencyType.EUR, TransactionType.IN,
                    "transaction", new BigDecimal(-10));
            mockMvc.perform(post("/tuum/v1/transactionHandler/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CommonUtilFunction.asJsonString(transactionDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @Transactional
    public void test_get_transaction_with_valid_accountId() throws Exception {
        MvcResult result = mockMvc.perform(get("/tuum/v1/transactionHandler/transactions/123456"))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        List<Transaction> transaction = new ObjectMapper().findAndRegisterModules().readValue(content,  new TypeReference<List<Transaction>>(){});
        assertEquals(2,transaction.size());
    }
}

