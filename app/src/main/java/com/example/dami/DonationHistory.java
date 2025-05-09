package com.example.dami;

public class DonationHistory {
    private String date;
    private String centerName;
    private String bloodType;
    private String status;

    public DonationHistory(String date, String centerName, String bloodType, String status) {
        this.date = date;
        this.centerName = centerName;
        this.bloodType = bloodType;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getStatus() {
        return status;
    }
} 