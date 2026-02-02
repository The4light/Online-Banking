package com.bankapp.backend.repository;

import com.bankapp.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find history for a specific account number, sorted by newest first
    List<Transaction> findByAccount_AccountNumberOrderByTimestampDesc(String accountNumber);
}