package com.tcd.asc.damn.common.model.request;

import com.tcd.asc.damn.common.model.dto.Coordinates;
import lombok.Data;

@Data
public class RouteRequest {
    private Coordinates startLocation;
    private Coordinates endLocation;
}