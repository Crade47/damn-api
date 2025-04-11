package com.tcd.asc.damn.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Trip {
    @Id
    private String tripId;
    private String serviceId;
    private String tripHeadsign;
    private String tripShortName;
    private int directionId;
    private String blockId;
    private String shapeId;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}