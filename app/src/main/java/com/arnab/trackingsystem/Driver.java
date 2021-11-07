package com.arnab.trackingsystem;

public class Driver {
    private String name;
    private String email;
    private String phone;
    private double longitude;
    private double latitude;

    public Driver() {
    }

    public Driver(String name, String email, String phone, double longitude, double latitude) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
