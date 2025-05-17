package com.example.dami.models;

public class JwtResponse {
    private String token;
    private Long userId;
    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 