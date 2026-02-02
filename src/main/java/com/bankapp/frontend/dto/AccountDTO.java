package com.bankapp.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Add this import
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String userEmail; // Add this!
}