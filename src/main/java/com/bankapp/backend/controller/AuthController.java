package com.bankapp.backend.controller;

import com.bankapp.backend.dto.AuthResponse;
import com.bankapp.backend.dto.LoginRequest;
import com.bankapp.backend.model.User;
import com.bankapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.bankapp.backend.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Note: The path is /api/auth
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        // Success: Return the AuthResponse
                        return ResponseEntity.ok(new AuthResponse(user.getFirstName(), user.getEmail()));
                    } else {
                        // Fail: Wrong password
                        return ResponseEntity.status(401).body("Invalid credentials");
                    }
                })
                // Fail: User not found
                .orElse(ResponseEntity.status(401).body("User not found"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // 1. Hash and Save the User
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // 2. Create the 3 default accounts for this new user
        accountService.createDefaultAccounts(savedUser);

        return ResponseEntity.ok(savedUser);
    }
}