package com.bankapp.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // This stops the "Unrecognized field" error
public class TransactionDTO {
    private String description;
    private BigDecimal amount;
    private String timestamp;

    // This helper method creates the clean date: "2026-02-02"
    public String getFormattedDate() {
        if (timestamp != null && timestamp.contains("T")) {
            return timestamp.split("T")[0];
        }
        return timestamp;
    }
}