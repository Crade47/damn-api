package com.tcd.asc.damn.common.repository;

import com.tcd.asc.damn.common.entity.LuasRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LuasRouteRepository extends JpaRepository<LuasRoute, Long> {
}
