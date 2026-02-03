package com.bankapp.backend.controller;

import com.bankapp.backend.model.Account;
import com.bankapp.backend.model.Transaction;
import com.bankapp.backend.repository.AccountRepository;
import com.bankapp.backend.model.User;
import com.bankapp.backend.repository.UserRepository;
import com.bankapp.backend.service.AccountService;
import com.bankapp.backend.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;


    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable String email) {
        try {
            // Log it so you can see exactly what email the frontend is sending
            System.out.println("Fetching accounts for email: " + email);

            List<AccountDTO> accounts = accountService.getAccountsByEmail(email);

            if (accounts.isEmpty()) {
                return ResponseEntity.status(404).body("No accounts found for user: " + email);
            }

            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}