package com.bankapp.backend.repository;

import com.bankapp.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // For Dashboard: Finds all accounts linked to the user's email
    List<Account> findByUserEmail(String email);

    // For Transfers: Finds one specific account to send money to
    Optional<Account> findByAccountNumber(String accountNumber);
}