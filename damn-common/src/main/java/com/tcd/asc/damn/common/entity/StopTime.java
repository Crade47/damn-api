package com.tcd.asc.damn.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Data
@IdClass(StopTime.StopTimeId.class)
public class StopTime {
    @Id
    @Column(name = "stop_id") // Explicitly map to the stop_id column
    private String stopId;

    @Id
    @Column(name = "trip_id") // Explicitly map to avoid ambiguity
    private String tripId;

    @Id
    @Column(name = "stop_sequence") // Explicitly map to avoid ambiguity
    private int stopSequence;

    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private String stopHeadsign;
    private int pickupType;
    private int dropOffType;
    private int timepoint;

    @ManyToOne
    @JoinColumn(name = "stop_id", insertable = false, updatable = false) // Reference the same column
    private Stop stop;

    @ManyToOne
    @JoinColumn(name = "trip_id", insertable = false, updatable = false) // Reference the same column
    private Trip trip;

    // Composite key class
    @Data
    public static class StopTimeId implements Serializable {
        private String tripId;
        private String stopId;
        private int stopSequence;

        // Default constructor for serialization
        public StopTimeId() {}

        // Constructor for creating the key
        public StopTimeId(String tripId, String stopId, int stopSequence) {
            this.tripId = tripId;
            this.stopId = stopId;
            this.stopSequence = stopSequence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StopTimeId that = (StopTimeId) o;
            return stopSequence == that.stopSequence &&
                    tripId.equals(that.tripId) &&
                    stopId.equals(that.stopId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tripId, stopId, stopSequence);
        }
    }
}