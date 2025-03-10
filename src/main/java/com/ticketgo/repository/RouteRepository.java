package com.ticketgo.repository;

import com.ticketgo.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Route findByRouteName(String routeName);

    List<Route> findAllByDepartureLocationContains(String departureLocation);
}
