package com.example.dami.models;

import java.time.LocalDateTime;

public class BloodDonationRequest {
    private String bloodType;
    private double quantity;
    private Long bloodCenterId;
    private String status;
    private BloodCenter bloodCenter;
    private LocalDateTime createdAt;

    public BloodDonationRequest() {
        this.createdAt = LocalDateTime.now();
    }

    public BloodDonationRequest(String bloodType, double quantity, Long bloodCenterId) {
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.bloodCenterId = bloodCenterId;
        this.status = "PENDING"; // Default status
        this.createdAt = LocalDateTime.now();
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Long getBloodCenterId() {
        return bloodCenterId;
    }

    public void setBloodCenterId(Long bloodCenterId) {
        this.bloodCenterId = bloodCenterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BloodCenter getBloodCenter() {
        return bloodCenter;
    }

    public void setBloodCenter(BloodCenter bloodCenter) {
        this.bloodCenter = bloodCenter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 