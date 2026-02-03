package com.bankapp.backend.service;

import com.bankapp.backend.dto.TransferRequest;
import com.bankapp.backend.model.*;
import com.bankapp.backend.enums.TransactionType;
import com.bankapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository; // You'll need this repo!

    @Transactional
    public String transferMoney(TransferRequest request) {
        Account fromAcc = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAcc = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new RuntimeException("Beneficiary account not found"));

        if (fromAcc.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds!");
        }

        fromAcc.setBalance(fromAcc.getBalance().subtract(request.getAmount()));
        toAcc.setBalance(toAcc.getBalance().add(request.getAmount()));

        // Record the transaction for the UI's "Recent Transfers" table
        Transaction tx = Transaction.builder()
                .amount(request.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .description(request.getDescription())
                .account(fromAcc)
                // Save receiver info so we can display it in the table
                .receiverEmail(toAcc.getUser().getFirstName() + " " + toAcc.getUser().getLastName())
                .build();

        transactionRepository.save(tx);
        return "Complete Transfer Successful";
    }
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        // This calls the Repository method we updated with the @Query
        return transactionRepository.findByAccountNumber(accountNumber);
    }
}