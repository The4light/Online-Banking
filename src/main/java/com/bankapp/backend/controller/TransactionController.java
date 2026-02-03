package com.bankapp.backend.controller;

import com.bankapp.backend.model.Transaction;
import com.bankapp.backend.service.AccountService;
import com.bankapp.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> request) {
        try {
            String sourceAcc = (String) request.get("sourceAccountNumber");
            String destAcc = (String) request.get("destinationAccountNumber");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");

            accountService.transferMoney(sourceAcc, destAcc, amount, description);

            return ResponseEntity.ok(Map.of("message", "Transfer Successful!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/transactions/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String accountNumber) {
        System.out.println("DEBUG: Fetching transactions for account: " + accountNumber);
        List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}