package com.bankapp.backend.controller;

import com.bankapp.backend.service.AccountService; // ADD THIS IMPORT
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

    private final AccountService accountService; // Changed from Repository to Service

    @GetMapping("/user/{email}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable String email) {
        List<AccountDTO> accounts = accountService.getAccountsByEmail(email);
        System.out.println("Backend: Sending " + accounts.size() + " accounts for " + email);
        return ResponseEntity.ok(accounts);
    }
}