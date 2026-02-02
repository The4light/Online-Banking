package com.bankapp.backend.dto;

import lombok.Data;

@Data // This generates getEmail() and getPassword() automatically
public class LoginRequest {
    private String email;
    private String password;
}