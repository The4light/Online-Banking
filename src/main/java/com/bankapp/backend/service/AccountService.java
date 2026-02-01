package com.bankapp.backend.service;

import com.bankapp.backend.model.Account;
import com.bankapp.backend.repository.AccountRepository;
import com.bankapp.backend.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDTO> getAccountsByEmail(String email) {
        // 1. Fetch raw Account entities from the DB using the user's email
        List<Account> accounts = accountRepository.findByUserEmail(email);

        // 2. Convert (Map) those entities into AccountDTOs to avoid JSON loops
        return accounts.stream()
                .map(acc -> new AccountDTO(
                        acc.getAccountNumber(),
                        acc.getBalance(),
                        acc.getAccountType().name() // Convert Enum to String
                ))
                .collect(Collectors.toList());
    }
}