package com.example.dami.models;

public class BloodDonationRequestResponse {
    private Long id;
    private String bloodType;
    private String status;
    private Double quantity;
    private Long requestedBy;
    private Long bloodCenter;

    public BloodDonationRequestResponse() {
    }

    public BloodDonationRequestResponse(Long id, String bloodType, String status,Double quantity, Long requestedBy, Long bloodCenter) {
        this.id = id;
        this.bloodType = bloodType;
        this.status = status;
        this.quantity = quantity;
        this.requestedBy = requestedBy;
        this.bloodCenter = bloodCenter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Long getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Long requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Long getBloodCenter() {
        return bloodCenter;
    }

    public void setBloodCenter(Long bloodCenter) {
        this.bloodCenter = bloodCenter;
    }
} 