package com.ticketgo.repository;

import com.ticketgo.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Route findByRouteName(String routeName);
}
