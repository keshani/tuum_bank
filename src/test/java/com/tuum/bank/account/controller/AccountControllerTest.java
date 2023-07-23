package com.tuum.bank.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.bank.CommonUtilFunction;
import com.tuum.bank.common.models.CurrencyType;
import com.tuum.bank.modules.account.mapper.AccountMapper;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.account.model.AccountDto;
import org.junit.jupiter.api.Assertions;
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
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountMapper accountMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_create_account_with_valid_data() throws Exception {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP, CurrencyType.SEK, CurrencyType.USD);
        AccountDto accountDto =  new AccountDto("testcustomer1", "Estonia", currencyType);
        MvcResult result = mockMvc.perform(post("/tuum/v1/accountsHandler/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CommonUtilFunction.asJsonString(accountDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").isNotEmpty()).andReturn();
        String content = result.getResponse().getContentAsString();

        Account account = new ObjectMapper().readValue(content, Account.class);
        assertAll(
                ()-> assertEquals("testcustomer1", account.getCustomerId() ),
                ()-> assertEquals("Estonia" , account.getCountry()),
                ()-> assertEquals( 4, account.getAccountBalanceList().size())
        );

        List<CurrencyType> balanceCurrency = account.getAccountBalanceList().stream()
                .map(AccountBalance::getCurrencyType)
                .toList();

        // Check if the actual names match the expected names
        Assertions.assertIterableEquals(currencyType, balanceCurrency);
    }

    @Test
    @Transactional
    public void test_create_account_with_invalid_currency() throws Exception {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP, CurrencyType.SEK, CurrencyType.USD);
        AccountDto accountDto =  new AccountDto("testInvalidCurrency", "Estonia", currencyType);
        String dto = CommonUtilFunction.asJsonString(accountDto).replace("EUR","EUWWW");
        mockMvc.perform(post("/tuum/v1/accountsHandler/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dto))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class test_create_account_without_required_fields{
        @Test
        void test_without_customerId() throws Exception {
            AccountDto accountDto = new AccountDto();
            accountDto.setCountry("Estonia");
            accountDto.setCurrencyTypeList(List.of(CurrencyType.EUR));
            mockMvc.perform(post("/tuum/v1/accountsHandler/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content( CommonUtilFunction.asJsonString(accountDto)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        void test_without_country() throws Exception {
            AccountDto accountDto = new AccountDto();
            accountDto.setCustomerId("TestCustomer");
            accountDto.setCurrencyTypeList(List.of(CurrencyType.EUR));
            mockMvc.perform(post("/tuum/v1/accountsHandler/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content( CommonUtilFunction.asJsonString(accountDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void test_without_currency() throws Exception {
            AccountDto accountDto = new AccountDto();
            accountDto.setCustomerId("TestCustomer");
            accountDto.setCountry("Estonia");
            mockMvc.perform(post("/tuum/v1/accountsHandler/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content( CommonUtilFunction.asJsonString(accountDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void test_get_account_with_valid_accountId() throws Exception {
        List<CurrencyType> currencyType = List.of(CurrencyType.EUR, CurrencyType.GBP, CurrencyType.SEK, CurrencyType.USD);
        AccountDto accountDto =  new AccountDto("testcustomer1", "Estonia", currencyType);

        MvcResult savedResult = mockMvc.perform(post("/tuum/v1/accountsHandler/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CommonUtilFunction.asJsonString(accountDto))).andReturn();
        String savedContent = savedResult.getResponse().getContentAsString();

        Account savedAccount = new ObjectMapper().readValue(savedContent, Account.class);
        MvcResult retriveResult = mockMvc.perform(get("/tuum/v1/accountsHandler/"+savedAccount.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").isNotEmpty()).andReturn();
        String retriveContent = retriveResult.getResponse().getContentAsString();

        Account retirveAccount = new ObjectMapper().readValue(retriveContent, Account.class);
        assertEquals(savedAccount.getAccountId(), retirveAccount.getAccountId());
    }

    @Test
    public void test_get_account_with_invalid_accountId() throws Exception {
        mockMvc.perform(get("/tuum/v1/accountsHandler/123"))
                .andExpect(status().isNotFound());
    }

}
