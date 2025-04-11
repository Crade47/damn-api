package com.tcd.asc.damn.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcd.asc.damn.common.constants.TransitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WalkSegment extends RouteSegment {
    private Coordinates startCoordinate;
    private Coordinates endCoordinate;
    @JsonProperty("walkPath")
    private List<Coordinates> walkPath;

    public WalkSegment() {
        super(TransitType.WALK);
    }
}