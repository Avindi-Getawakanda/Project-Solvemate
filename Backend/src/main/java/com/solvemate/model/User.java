package com.solvemate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String status;

    private boolean isVerified;

    private String verificationToken;

    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.status    = "ACTIVE";
        this.isVerified = false;
    }

    public Long          getUserId()            { return userId; }
    public String        getFullName()          { return fullName; }
    public String        getEmail()             { return email; }
    public String        getPasswordHash()      { return passwordHash; }
    public String        getRole()              { return role; }
    public String        getStatus()            { return status; }
    public boolean       isVerified()           { return isVerified; }
    public String        getVerificationToken() { return verificationToken; }
    public LocalDateTime getCreatedAt()         { return createdAt; }

    public void setUserId(Long userId)                  { this.userId = userId; }
    public void setFullName(String fullName)            { this.fullName = fullName; }
    public void setEmail(String email)                  { this.email = email; }
    public void setPasswordHash(String passwordHash)    { this.passwordHash = passwordHash; }
    public void setRole(String role)                    { this.role = role; }
    public void setStatus(String status)                { this.status = status; }
    public void setVerified(boolean verified)           { this.isVerified = verified; }
    public void setVerificationToken(String token)      { this.verificationToken = token; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
}