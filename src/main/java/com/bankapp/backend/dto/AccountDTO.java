package com.bankapp.backend.dto;

import lombok.*;
import java.math.BigDecimal;

@Data // This automatically generates Getters, Setters, equals, canEqual, hashCode, and toString
@NoArgsConstructor // Necessary for Jackson to create the object from JSON
@AllArgsConstructor // This creates the constructor used in your AccountService (accountNumber, balance, type)
@Builder // Optional: allows for easier object creation
public class AccountDTO {
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
}