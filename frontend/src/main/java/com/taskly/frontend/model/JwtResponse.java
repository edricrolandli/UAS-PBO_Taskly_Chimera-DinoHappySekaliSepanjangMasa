package com.taskly.frontend.model;

public class JwtResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;

    // Getter & Setter Manual
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
