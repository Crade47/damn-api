package com.tcd.asc.damn.common.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;
    private String phoneNumber;
    private String website;
    private Double rating;
    private String openingHours;
    private String imageUrl;
    private String locationType;

    // No-argument constructor required by JPA
    public Location() {}
}