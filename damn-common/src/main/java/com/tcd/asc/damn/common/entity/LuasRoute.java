package com.tcd.asc.damn.common.entity;

import com.tcd.asc.damn.common.model.dto.LuasRouteId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "luas_route")
@IdClass(LuasRouteId.class)
@Data
public class LuasRoute {
    @Id
    @ManyToOne
    @JoinColumn(name = "from_station_id", nullable = false)
    private Station fromStation;

    @Id
    @ManyToOne
    @JoinColumn(name = "to_station_id", nullable = false)
    private Station toStation;
}


