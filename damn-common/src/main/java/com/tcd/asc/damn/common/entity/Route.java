package com.tcd.asc.damn.common.entity;

import com.tcd.asc.damn.common.constants.TransitType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Route {
    @Id
    @Column(name = "route_id")
    private String routeId;
    private String agencyId;
    private String routeShortName;
    private String routeLongName;
    private String routeDesc;
    private int routeType; // GTFS route_type: 0 for Luas (tram), 3 for Bus
    private String routeUrl;
    private String routeColor;
    private String routeTextColor;

    @Enumerated(EnumType.STRING)
    private TransitType transitType;
}