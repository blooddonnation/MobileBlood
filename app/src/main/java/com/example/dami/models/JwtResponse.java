package com.example.dami.models;

import com.google.gson.annotations.SerializedName;

public class JwtResponse {
    @SerializedName("token")
    private String token;
    
    @SerializedName("userId")
    private Long userId;

    public JwtResponse() {
    }

    public JwtResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                '}';
    }
} 