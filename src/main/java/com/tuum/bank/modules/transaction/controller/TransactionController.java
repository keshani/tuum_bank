package com.tuum.bank.modules.transaction.controller;

import com.tuum.bank.exception.exceptionType.AccountNotFoundException;
import com.tuum.bank.modules.transaction.model.Transaction;
import com.tuum.bank.modules.transaction.model.TransactionDto;
import com.tuum.bank.modules.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tuum/v1/transactionHandler")
public class TransactionController {

    Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> addTransaction(@Valid @RequestBody TransactionDto transactionDto) throws AccountNotFoundException {
        Transaction transaction;
      try {
          transaction = transactionService.saveTransaction(transactionDto);
          LOGGER.info("Added new transaction for the account: "+transactionDto.getAccountId());

          // Publish message to account creation
          try {
              transactionService.publishMessageToQueue(transaction);
              LOGGER.info("Succesfully published message to the queue");

          } catch (Exception ex) {
              LOGGER.error("TransactionController::error occured while publishing msg Error", ex);
          }
      } catch (Exception ex) {
          LOGGER.error("TransactionController::addTransaction Error", ex);
          throw ex;
      }
      return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<List<Transaction>> getAccountById(@PathVariable("accountId") String accountId) throws AccountNotFoundException {
        try {
            List<Transaction> transactionList = transactionService.getTransactionsByAccountId(accountId);
            return  ResponseEntity.ok(transactionList);
        } catch (Exception ex) {
            LOGGER.error("TransactionController::getAccountById Error", ex);
            throw ex;
        }
    }

}
