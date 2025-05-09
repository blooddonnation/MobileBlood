package com.example.dami;

public class BloodCenter {
    private Long id;
    private String nameadmin;
    private String nameCenter;
    private String address;
    private String phone;
    private double latitude;
    private double longitude;

    public BloodCenter(Long id, String name, String namecenter) {
        this.id = id;
        this.nameadmin = name;
        this.nameCenter = namecenter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameadmin() {
        return nameadmin;
    }

    public void setNameadmin(String nameadmin) {
        this.nameadmin = nameadmin;
    }

    public String getNamecenter() {
        return nameCenter;
    }

    public void setNamecenter(String namecenter) {
        this.nameCenter = namecenter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
