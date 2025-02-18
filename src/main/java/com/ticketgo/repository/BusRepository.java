package com.ticketgo.repository;

import com.ticketgo.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    @Query("""
        select s.bus from Schedule s
        where s.scheduleId = :scheduleId
    """)
    Optional<Bus> findBySchedule(Long scheduleId);
}
