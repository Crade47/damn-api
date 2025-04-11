package com.tcd.asc.damn.common.entity;

import com.tcd.asc.damn.common.constants.TransitType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Stop {
    @Id
    private String stopId;
    private String stopCode;
    private String stopName;
    private String stopDesc;
    private double stopLat;
    private double stopLon;
    private String zoneId;
    private String stopUrl;
    private String locationType;
    private String parentStation;

    @Enumerated(EnumType.STRING)
    private TransitType stopType;
}