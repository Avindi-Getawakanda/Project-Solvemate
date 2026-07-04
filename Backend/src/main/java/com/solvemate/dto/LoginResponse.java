package com.solvemate.dto;

public class LoginResponse {

    private String message;
    private String role;
    private String fullName;
    private String email;

    public LoginResponse() {}

    public LoginResponse(String message, String role, String fullName, String email) {
        this.message  = message;
        this.role     = role;
        this.fullName = fullName;
        this.email    = email;
    }

    public String getMessage()  { return message; }
    public String getRole()     { return role; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }

    public void setMessage(String message)   { this.message = message; }
    public void setRole(String role)         { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email)       { this.email = email; }
}