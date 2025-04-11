package com.tcd.asc.damn.common.entity;

import com.tcd.asc.damn.common.constants.RouteName;
import com.tcd.asc.damn.common.constants.StationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stations")
@Data
public class Station {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationType type;

    @Enumerated(EnumType.STRING)
    private RouteName routeName;
}


