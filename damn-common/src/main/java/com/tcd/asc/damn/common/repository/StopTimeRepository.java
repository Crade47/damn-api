package com.tcd.asc.damn.common.repository;

import com.tcd.asc.damn.common.entity.StopTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, StopTime.StopTimeId> {
    List<StopTime> findByTripId(String tripId);
    List<StopTime> findByStopId(String stopId);
}