package com.example.dami.models;

public class BloodCenter {
    private Long id;
    private Long idAdmin;
    private String latitude;
    private String longitude;
    private String name;
    private String location;

    public BloodCenter() {
    }

    public BloodCenter(Long id, Long idAdmin, String latitude, String longitude, String name, String location) {
        this.id = id;
        this.idAdmin = idAdmin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Long idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return name; // This will be used for the dropdown display
    }
}
