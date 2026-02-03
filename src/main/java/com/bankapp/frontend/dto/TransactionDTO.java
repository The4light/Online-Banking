package com.bankapp.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // This stops the "Unrecognized field" error
public class TransactionDTO {
    private String description;
    private BigDecimal amount;
    private LocalDateTime timestamp; // This MUST match the Backend field name

    // Jackson needs this for the TableView to find the value
    public String getFormattedDate() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Getters and Setters for description and amount...
}