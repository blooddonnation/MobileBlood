package com.example.dami.models;

import com.google.gson.annotations.SerializedName;

public class BloodDonationRequest {
    @SerializedName("id")
    private Long id;

    @SerializedName("bloodType")
    private String bloodType;

    @SerializedName("status")
    private String status;

    @SerializedName("quantity")
    private double quantity;

    @SerializedName("requestedBy")
    private Long requestedBy;

    @SerializedName("bloodCenter")
    private Long bloodCenter;

    public BloodDonationRequest() {
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

    public void setQuantity(Double quantity) {
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

    @Override
    public String toString() {
        return "BloodDonationRequest{" +
                "id=" + id +
                ", bloodType='" + bloodType + '\'' +
                ", status='" + status + '\'' +
                ", quantity=" + quantity +
                ", requestedBy=" + requestedBy +
                ", bloodCenter=" + bloodCenter +
                '}';
    }
} 