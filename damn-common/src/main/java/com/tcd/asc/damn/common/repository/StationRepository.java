package com.tcd.asc.damn.common.repository;


import com.tcd.asc.damn.common.constants.StationType;
import com.tcd.asc.damn.common.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByType(StationType type);
}
