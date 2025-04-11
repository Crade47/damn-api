package com.tcd.asc.damn.common.repository;

import com.tcd.asc.damn.common.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopRepository extends JpaRepository<Stop, String> {
    List<Stop> findAll(); // For finding nearest stops
}