package com.example.dami.models;

public class DonationHistory {
    private String centerName;
    private String bloodType;
    private String date;
    private String status;

    public DonationHistory() {
    }

    public DonationHistory(String centerName, String bloodType, String date, String status) {
        this.centerName = centerName;
        this.bloodType = bloodType;
        this.date = date;
        this.status = status;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 