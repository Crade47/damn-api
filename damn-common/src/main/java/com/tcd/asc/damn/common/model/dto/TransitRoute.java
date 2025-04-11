package com.tcd.asc.damn.common.model.dto;

import com.tcd.asc.damn.common.constants.TransitType;
import com.tcd.asc.damn.common.entity.Stop;
import lombok.Data;

import java.util.List;

@Data
public class TransitRoute {
    private Stop boardingStop;
    private Stop alightingStop;
    private Double travelDistance;
    private Double travelCost;
    private Double travelTime;
    private List<Stop> stopPath;
    private List<Coordinates> transitPath;
    private TransitType transitType;
}

