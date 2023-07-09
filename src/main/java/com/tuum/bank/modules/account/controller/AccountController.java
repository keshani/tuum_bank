package com.tuum.bank.modules.account.controller;

import com.tuum.bank.exception.exceptionType.AccountNotFoundException;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountDto;
import com.tuum.bank.modules.account.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tuum.bank.messaging.MessageQueueNames.ACCOUNT_CREATION;

@RestController
@RequestMapping("/tuum/v1/accountsHandler")
public class AccountController {
    private final AccountService accountService;
    Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable("accountId") String accountId) throws AccountNotFoundException {
        try {
            Account account = accountService.getAccountById(accountId);
            if (account == null) {
                throw new AccountNotFoundException("No account found for given accountid");
            }
            return ResponseEntity.ok(account);
        } catch (Exception ex) {
            LOGGER.error("AccountController::getAccountById Error", ex);
            throw ex;
        }
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDto accountDto) {
        Account account;
        try {
            account = accountService.saveAccount(accountDto);
            LOGGER.info("Create New Account with AccountId: " + account + " for customer id: " + account.getCustomerId());
            // Publish message to account creation
            try {
                accountService.publishMessageToQueue(ACCOUNT_CREATION, account);
                LOGGER.info("Succesfully published message to the queue ");
            } catch (Exception ex) {
                LOGGER.error("AccountController::error occured while publishing msg Error", ex);
            }
        } catch (Exception ex) {
            LOGGER.error("AccountController::createAccount Error", ex);
            throw ex;
        }
        return ResponseEntity.ok(account);
    }

}
