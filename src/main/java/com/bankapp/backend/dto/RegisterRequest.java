package com.bankapp.backend.dto;

import lombok.Data;

@Data // <--- This is vital! It generates the getters and setters automatically.
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
}