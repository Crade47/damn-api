package com.tcd.asc.damn.common.model.response;

public class LocationResponse {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;
    private String phoneNumber;    // Optional phone number
    private String website;        // Optional website URL
    private Double rating;         // Optional rating
    private String openingHours;   // Optional opening hours
    private String imageUrl;       // Optional image URL

    public LocationResponse() {
    }

    // Constructor with all fields
    public LocationResponse(String name, String address, double latitude, double longitude, String description,
                            String phoneNumber, String website, Double rating, String openingHours, String imageUrl) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.rating = rating;
        this.openingHours = openingHours;
        this.imageUrl = imageUrl;
    }

    // Constructor with only mandatory fields, setting optional fields to null
    public LocationResponse(String name, String address, double latitude, double longitude, String description) {
        this(name, address, latitude, longitude, description,
                null, null, null, null, null);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}