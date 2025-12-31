package com.example.real_estate_backend.model;



import jakarta.persistence.Embeddable;

@Embeddable
public class Location {

    private String address;
    private String city;
    private Double latitude;
    private Double longitude;

    // getter & setter
    public String getAddress() {
        return address;
    }
    public String getCity() {
        return city;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
