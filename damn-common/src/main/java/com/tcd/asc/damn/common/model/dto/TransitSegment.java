package com.tcd.asc.damn.common.model.dto;

import com.tcd.asc.damn.common.constants.TransitType;
import com.tcd.asc.damn.common.entity.Stop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransitSegment extends RouteSegment {
    private Stop boardingStop;
    private Stop alightingStop;
    private Double travelDistance;
    private Double travelCost;
    private Double travelTime;
    private List<Stop> stopPath;
    private List<Coordinates> transitPath;

    public TransitSegment() {
        super(TransitType.LUAS);
    }
}