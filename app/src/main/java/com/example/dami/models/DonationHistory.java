package com.example.dami.models;

public class DonationHistory {
    private String recipientName;
    private String donationDate;
    private int volume;
    private String location;

    public DonationHistory() {
    }

    public DonationHistory(String recipientName, String donationDate, int volume, String location) {
        this.recipientName = recipientName;
        this.donationDate = donationDate;
        this.volume = volume;
        this.location = location;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
