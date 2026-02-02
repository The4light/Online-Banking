package com.bankapp.backend.service;

import com.bankapp.backend.enums.TransactionType;
import com.bankapp.backend.model.Account;
import com.bankapp.backend.model.Transaction;
import com.bankapp.backend.model.User;
import com.bankapp.backend.repository.AccountRepository;
import com.bankapp.backend.dto.AccountDTO;
import com.bankapp.backend.enums.AccountType; // Check your package name here
import com.bankapp.backend.enums.AccountStatus; // Check your package name here
import com.bankapp.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // This is the method AuthController is looking for!
    public void createDefaultAccounts(User user) {
        try {
            // Generate ONE shared number
            String sharedNumber = "90" + (100000000L + new java.util.Random().nextLong(900000000L));

            // We will use ONE helper method name to avoid confusion
            saveNewAccount(user, AccountType.CHECKING, new BigDecimal("5000.00"), sharedNumber);
            saveNewAccount(user, AccountType.SAVINGS, new BigDecimal("10000.00"), sharedNumber);
            saveNewAccount(user, AccountType.INVESTMENT, new BigDecimal("25000.00"), sharedNumber);

            System.out.println("DEBUG: Default accounts created successfully for " + user.getEmail());
        } catch (Exception e) {
            System.err.println("ERROR creating accounts: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Account creation failed: " + e.getMessage());
        }
    }

    private void saveAcc(User user, AccountType type, BigDecimal bal, String num) {
        Account acc = Account.builder()
                .accountNumber(num) // They all get the same number
                .balance(bal)
                .accountType(type)
                .user(user)
                .status(AccountStatus.ACTIVE)
                .build();
        accountRepository.save(acc);
    }


    private void saveNewAccount(User user, AccountType type, java.math.BigDecimal balance, String accountNumber) {
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .accountType(type)
                .user(user)
                .status(AccountStatus.ACTIVE)
                .build();
        accountRepository.save(account);
    }

    public List<AccountDTO> getAccountsByEmail(String email) {
        return accountRepository.findByUserEmail(email).stream()
                .map(acc -> {
                    AccountDTO dto = new AccountDTO();
                    dto.setAccountNumber(acc.getAccountNumber());
                    dto.setBalance(acc.getBalance());
                    dto.setAccountType(acc.getAccountType().name());
                    // THIS IS THE LINE THAT SAVES THE DAY
                    dto.setUserEmail(acc.getUser().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void transferMoney(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount, String description) {
        // 1. Find the sender
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        // 2. Find the receiver
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // 3. Check if sender has enough money
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in source account");
        }

        // 4. Update balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // 5. Save updated accounts
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // 6. Record the transaction history for the sender
        Transaction debit = Transaction.builder()
                .amount(amount.negate()) // Negative because it's leaving
                .description("Transfer to " + destinationAccountNumber + ": " + description)
                .transactionType(TransactionType.TRANSFER) // Make sure TRANSFER exists in your Enum
                .account(sourceAccount)
                .senderEmail(sourceAccount.getUser().getEmail())
                .receiverEmail(destinationAccount.getUser().getEmail())
                .timestamp(LocalDateTime.now())
                .build();

        Transaction credit = Transaction.builder()
                .amount(amount)
                .description("Transfer from " + sourceAccountNumber + ": " + description)
                .transactionType(TransactionType.TRANSFER)
                .account(destinationAccount)
                .timestamp(LocalDateTime.now())
                .build();

        // Note: We need to inject TransactionRepository into this service to save this
         transactionRepository.save(debit);
         transactionRepository.save(credit);
    }
    // Inside AccountService.java
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccount_AccountNumberOrderByTimestampDesc(accountNumber);
    }
}