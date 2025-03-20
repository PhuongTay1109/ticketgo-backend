package com.ticketgo.repository;

import com.ticketgo.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Driver d
        SET d.isDeleted = true
        WHERE d.driverId = :id
    """)
    void softDelete(Long id);

    @Query("""
        SELECT d FROM Driver d
        WHERE d.driverId NOT IN (
            SELECT s.driver.driverId FROM Schedule s
            WHERE (
            (:departureTime BETWEEN s.departureTime AND s.arrivalTime)
            OR (:arrivalTime BETWEEN s.departureTime AND s.arrivalTime)
            OR (s.departureTime BETWEEN :departureTime AND :arrivalTime)
        )
        )
    """)
    List<Driver> findAvailableDrivers(LocalDateTime departureTime, LocalDateTime arrivalTime);
}
