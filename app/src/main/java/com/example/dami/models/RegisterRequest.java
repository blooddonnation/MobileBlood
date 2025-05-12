package com.example.dami.models;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class RegisterRequest {
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("bloodType")
    private String bloodType;
    
    @SerializedName("dateOfBirth")
    private String dateOfBirth;
    
    @SerializedName("role")
    private String role;

    public RegisterRequest(String username, String password, String email, String bloodType, LocalDate dateOfBirth, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.bloodType = bloodType;
        this.dateOfBirth = dateOfBirth.toString();
        this.role = role;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
} 