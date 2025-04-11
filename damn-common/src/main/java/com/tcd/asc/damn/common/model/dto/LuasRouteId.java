package com.tcd.asc.damn.common.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class LuasRouteId implements Serializable {

    private String fromStation;
    private String toStation;

    public LuasRouteId() {}

    public LuasRouteId(String fromStation, String toStation) {
        this.fromStation = fromStation;
        this.toStation = toStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LuasRouteId that = (LuasRouteId) o;
        return Objects.equals(fromStation, that.fromStation) &&
               Objects.equals(toStation, that.toStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromStation, toStation);
    }
}
