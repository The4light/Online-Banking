package com.bankapp.backend.service;

import com.bankapp.backend.dto.RegisterRequest;
import com.bankapp.backend.enums.AccountStatus;
import com.bankapp.backend.enums.AccountType;
import com.bankapp.backend.model.Account;
import com.bankapp.backend.model.User;
import com.bankapp.backend.repository.AccountRepository;
import com.bankapp.backend.repository.UserRepository;
import com.bankapp.backend.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Good practice for multi-saves

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // ADDED THIS

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Transactional // Ensures if one account fails, none are created
    public String registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        // 1. Create and save the User FIRST
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // 2. Logic for 10-digit account number base
        String rawPhone = request.getPhoneNumber();
        String accountNumberBase = rawPhone.startsWith("0") ? rawPhone.substring(1) : rawPhone;

        // 3. Create the 3 types seen in your UI
        // We append 1, 2, and 3 to make them unique
        Account checking = createAccount(savedUser, accountNumberBase + "1", AccountType.CHECKING, 5000.00);
        Account savings = createAccount(savedUser, accountNumberBase + "2", AccountType.SAVINGS, 10000.00);
        Account investment = createAccount(savedUser, accountNumberBase + "3", AccountType.INVESTMENT, 25000.00);

        accountRepository.saveAll(List.of(checking, savings, investment));

        return "User registered successfully with Checking, Savings, and Investment accounts!";
    }

    // HELPER METHOD (Moved outside of registerUser)
    private Account createAccount(User user, String number, AccountType type, double initialBalance) {
        return Account.builder()
                .accountNumber(number)
                .balance(BigDecimal.valueOf(initialBalance))
                .accountType(type)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();
    }
}