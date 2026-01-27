package com.bankapp.backend.models;

import com.bankapp.backend.enums.AccountStatus;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {

    private ObjectId id;
    private ObjectId userId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public Account(ObjectId userId, String accountNumber) {
        this();
        this.userId = userId;
        this.accountNumber = accountNumber;
    }

    // ===== GETTERS & SETTERS =====

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
