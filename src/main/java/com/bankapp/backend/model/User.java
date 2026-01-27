package com.bankapp.backend.models;

import com.bankapp.backend.enums.Role;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class User {

    private ObjectId id;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.role = Role.USER;
    }

    public User(String fullName, String email, String passwordHash) {
        this();
        this.fullName = fullName;
        this.email = email.toLowerCase();
        this.passwordHash = passwordHash;
    }

    // ===== GETTERS & SETTERS =====

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
