package com.ticketgo.repository;

import com.ticketgo.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    @Query("""
            select rs from RouteStop rs
            where rs.schedule.scheduleId = :scheduleId
            and rs.stopType = com.ticketgo.enums.StopType.PICKUP
            order by rs.stopOrder asc
            """)
    List<RouteStop> getPickupRouteStopsByScheduleId(long scheduleId);

    @Query("""
            select rs from RouteStop rs
            where rs.schedule.scheduleId = :scheduleId
            and rs.stopType = com.ticketgo.enums.StopType.DROPOFF
            order by rs.stopOrder asc
            """)
    List<RouteStop> getDropoffRouteStopsByScheduleId(long scheduleId);


}
