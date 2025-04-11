package com.tcd.asc.damn.common.model.response;

import com.tcd.asc.damn.common.constants.TransitType;
import com.tcd.asc.damn.common.model.dto.RouteSegment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponse {
    private String routeId;
    private List<TransitType> uniqueTransitTypes;
    private List<RouteSegment> routes;
}