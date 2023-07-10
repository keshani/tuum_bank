package com.tuum.bank.modules.account.service;

import com.tuum.bank.messaging.Message;
import com.tuum.bank.messaging.MessageService;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.mapper.AccountMapper;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.account.model.AccountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountMapper accountMapper;
    private final AccountBalanceMapper accountBalanceMapper;
    private final MessageService messageService;

    Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    public AccountService(AccountMapper accountMapper, AccountBalanceMapper accountBalanceMapper, MessageService messageService) {
        this.accountMapper = accountMapper;
        this.accountBalanceMapper = accountBalanceMapper;
        this.messageService = messageService;
    }

    public Account getAccountById(String accountId) {
        return accountMapper.getAccountWithBalances(accountId);
    }

    @Transactional
    public Account saveAccount(AccountDto accountDto) {
        String accountId = UUID.randomUUID().toString();
        accountDto.setAccountId(accountId);
        accountMapper.insertAccount(accountDto);
        LOGGER.info("Create New Account with account id: "+ accountId);

        accountDto.getCurrencyTypeList().forEach((currency) -> {
            AccountBalance accBalance = new AccountBalance();
            accBalance.setAccountId(accountId);
            accBalance.setCurrencyType(currency);
            this.accountBalanceMapper.insertAccountBalance(accBalance);
        });

        LOGGER.info("Create New Account balances for account id: "+ accountId);
        return accountMapper.getAccountWithBalances(accountId);
    }

    public boolean publishMessageToQueue(String queue, Account account) {
        Message<Account> msg = new Message<Account>(queue, account.toString(), account );
        return this.messageService.sendMessage(msg);
    }
}
