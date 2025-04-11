package com.tcd.asc.damn.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@IdClass(Shape.ShapeId.class)
public class Shape {
    @Id
    private String shapeId;
    private double shapePtLat;
    private double shapePtLon;
    @Id
    private int shapePtSequence;
    private double shapeDistTraveled;

    // Composite key class
    @Data
    public static class ShapeId implements Serializable {
        private String shapeId;
        private int shapePtSequence;
    }
}