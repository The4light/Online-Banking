package com.bankapp.backend.service;

import com.bankapp.backend.dto.RegisterRequest;
import com.bankapp.backend.model.User;
import com.bankapp.backend.repository.UserRepository;
import com.bankapp.backend.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // MISSING IMPORT
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ADD THIS LINE

    public String registerUser(RegisterRequest request) {
        // 1. Check if user exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        // 2. Create new User entity
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // This will now work because passwordEncoder is declared above
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setPhoneNumber(request.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);

        // 3. Save to DB
        userRepository.save(user);

        return "User registered successfully!";
    }
}