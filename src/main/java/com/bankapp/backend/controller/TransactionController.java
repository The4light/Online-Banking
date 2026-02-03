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
            // 1. Extract values with basic null checking
            String sourceAcc = (String) request.get("sourceAccountNumber");
            String destAcc = (String) request.get("destinationAccountNumber");
            String description = (String) request.get("description");

            // 2. Safe BigDecimal Conversion
            Object amountObj = request.get("amount");
            if (amountObj == null || amountObj.toString().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Amount is required"));
            }
            BigDecimal amount = new BigDecimal(amountObj.toString());

            // 3. Service Call
            accountService.transferMoney(sourceAcc, destAcc, amount, description != null ? description : "Transfer");

            return ResponseEntity.ok(Map.of("message", "Transfer Successful!"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid amount format"));
        } catch (Exception e) {
            // This will catch your custom errors like "Insufficient Funds" from the service
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