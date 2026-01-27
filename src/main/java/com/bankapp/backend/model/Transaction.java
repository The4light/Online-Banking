package com.bankapp.backend.models;

import com.bankapp.backend.enums.TransactionType;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private ObjectId id;
    private ObjectId fromAccountId;
    private ObjectId toAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private String reference;
    private LocalDateTime createdAt;

    public Transaction() {
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(
            ObjectId fromAccountId,
            ObjectId toAccountId,
            BigDecimal amount,
            TransactionType type,
            String reference
    ) {
        this();
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.reference = reference;
    }

    // ===== GETTERS & SETTERS =====

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(ObjectId fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public ObjectId getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(ObjectId toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
