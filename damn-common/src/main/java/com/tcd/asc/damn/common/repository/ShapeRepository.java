package com.tcd.asc.damn.common.repository;

import com.tcd.asc.damn.common.entity.Shape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShapeRepository extends JpaRepository<Shape, Shape.ShapeId> {
    List<Shape> findByShapeId(String shapeId);
}