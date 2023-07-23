package com.tuum.bank.modules.transaction.service;

import com.tuum.bank.common.models.TransactionType;
import com.tuum.bank.exception.exceptionType.AccountNotFoundException;
import com.tuum.bank.exception.exceptionType.InsufficientBalanceException;
import com.tuum.bank.exception.exceptionType.InvalidTransactionAmountException;
import com.tuum.bank.messaging.Message;
import com.tuum.bank.messaging.MessageQueueNames;
import com.tuum.bank.messaging.MessageService;
import com.tuum.bank.modules.account.mapper.AccountBalanceMapper;
import com.tuum.bank.modules.account.mapper.AccountMapper;
import com.tuum.bank.modules.account.model.Account;
import com.tuum.bank.modules.account.model.AccountBalance;
import com.tuum.bank.modules.transaction.mapper.TransactionMapper;
import com.tuum.bank.modules.transaction.model.Transaction;
import com.tuum.bank.modules.transaction.model.TransactionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountBalanceMapper accountBalanceMapper;
    private final AccountMapper accountMapper;
    private final MessageService messageService;

    public TransactionService(TransactionMapper transactionMapper, AccountBalanceMapper accountBalanceMapper, AccountMapper accountMapper, MessageService messageService) {
        this.transactionMapper = transactionMapper;
        this.accountBalanceMapper = accountBalanceMapper;
        this.accountMapper = accountMapper;
        this.messageService = messageService;
    }

    @Transactional
    public  Transaction saveTransaction(TransactionDto transactionDto) throws AccountNotFoundException {
        Transaction transaction = mapTransactionDtoToTranaction(transactionDto);

         Object lock = new Object();
        synchronized(lock) {

            AccountBalance accountBalance = this.accountBalanceMapper.getBalanceByAccountIdAndCurrency(transactionDto.getAccountId()
                    , transactionDto.getCurrencyType().toString());

            // validate the transaction before saving
            this.validateTransaction(transactionDto, accountBalance);


            // set the remaining balance
            BigDecimal remainingBalance = calculateRemainingBalance(transactionDto, accountBalance);
            transaction.setBalance(remainingBalance);

            //save the transaction
            this.transactionMapper.saveTransaction(transaction);
            //update the balance in the relevent account
            this.accountBalanceMapper.updateAccountBalance(accountBalance.getId(), remainingBalance);

        }
        return transaction;
    }

    public List<Transaction> getTransactionsByAccountId(String accountId) throws AccountNotFoundException {
        Account account = this.accountMapper.getAccount(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account Not Found: " + accountId);
        }
        return this.transactionMapper.getTransactionsByAccountId(accountId);
    }

    private Transaction mapTransactionDtoToTranaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setAccountId(transactionDto.getAccountId());
        transaction.setCurrencyType(transactionDto.getCurrencyType());
        transaction.setTransDescription(transactionDto.getTransDescription());
        transaction.setTransferAmount(transactionDto.getAmount());
        transaction.setTransactionDatetime(LocalDateTime.now());
        return transaction;
    }

    public void validateTransaction(TransactionDto transactionDto, AccountBalance accountBalance){
        if (accountBalance == null) {
            throw new RuntimeException();
           // throw new AccountNotFoundException("There is no account with given currency type: " + transactionDto.getAccountId());
        }
        // check the transction amount is less than or equal to zero
        if (transactionDto.getAmount() == null ||
                transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException("Transaction ammout can not be empty or less than 0: " + transactionDto.getAccountId());
        }

        // check the transction type is OUT and the amount is greater than the avilable balance
        if (0 > accountBalance.getBalance().compareTo(transactionDto.getAmount())
                && TransactionType.OUT.equals(transactionDto.getTransactionType())) {
            throw new InsufficientBalanceException("Insufficient balance in account for given currency type: " + transactionDto.getAccountId());
        }
    }

    public BigDecimal calculateRemainingBalance(TransactionDto transactionDto, AccountBalance accountBalance) {
        BigDecimal remainingBalance;
        // if transction type is "IN" add amount to the available balance
        if (TransactionType.IN.equals(transactionDto.getTransactionType())) {
            remainingBalance = accountBalance.getBalance().add(transactionDto.getAmount());
        } else {
            // if transction type is "OUT" subtract amount from the available balance
            remainingBalance = accountBalance.getBalance().subtract(transactionDto.getAmount());
        }
        return remainingBalance;
    }

    public boolean publishMessageToQueue(Transaction transaction) {
        String queueName = MessageQueueNames.CREDIT_TRANSACTION;
        if (TransactionType.OUT.equals(transaction.getTransactionType())) {
            queueName = MessageQueueNames.DEBIT_TRANSACTION;
        }
        Message<Transaction> msg = new Message<Transaction>(queueName, transaction.toString(), transaction);
        return this.messageService.sendMessage(msg);
    }

}
