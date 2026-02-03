package com.bankapp.backend.service;

import com.bankapp.backend.enums.TransactionType;
import com.bankapp.backend.model.Account;
import com.bankapp.backend.model.Transaction;
import com.bankapp.backend.model.User;
import com.bankapp.backend.repository.AccountRepository;
import com.bankapp.backend.dto.AccountDTO;
import com.bankapp.backend.enums.AccountType;
import com.bankapp.backend.enums.AccountStatus;
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

    // Simplified: Now only creates ONE checking account
    public void createDefaultAccounts(User user) {
        try {
            String accountNumber = "90" + (10000000 + new Random().nextInt(90000000));

            Account account = Account.builder()
                    .accountNumber(accountNumber)
                    .balance(new BigDecimal("5000.00")) // Starting balance
                    .accountType(AccountType.CHECKING)
                    .user(user)
                    .status(AccountStatus.ACTIVE)
                    .build();

            accountRepository.save(account);
            System.out.println("DEBUG: Single account created: " + accountNumber);
        } catch (Exception e) {
            throw new RuntimeException("Account creation failed: " + e.getMessage());
        }
    }

    public List<AccountDTO> getAccountsByEmail(String email) {
        return accountRepository.findByUserEmail(email).stream()
                .map(acc -> {
                    AccountDTO dto = new AccountDTO();
                    dto.setAccountNumber(acc.getAccountNumber());
                    dto.setBalance(acc.getBalance());
                    dto.setAccountType(acc.getAccountType().name());
                    dto.setUserEmail(acc.getUser().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void transferMoney(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount, String description) {
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Record for SENDER
        Transaction debit = Transaction.builder()
                .amount(amount.negate())
                .description("Transfer to " + destinationAccountNumber + ": " + description)
                .transactionType(TransactionType.TRANSFER)
                .account(sourceAccount)
                .senderEmail(sourceAccount.getUser().getEmail())
                .receiverEmail(destinationAccount.getUser().getEmail())
                .timestamp(LocalDateTime.now())
                .build();

        // Record for RECEIVER
        Transaction credit = Transaction.builder()
                .amount(amount)
                .description("Transfer from " + sourceAccountNumber + ": " + description)
                .transactionType(TransactionType.TRANSFER)
                .account(destinationAccount)
                .senderEmail(sourceAccount.getUser().getEmail())
                .receiverEmail(destinationAccount.getUser().getEmail())
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(debit);
        transactionRepository.save(credit);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }
}