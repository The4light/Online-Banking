package com.bankapp.frontend.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    // These MUST match the names in the browser exactly
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
}