package com.ticketgo.repository;

import com.ticketgo.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {
    @Query("""
        SELECT s.driver.driverId
        FROM Schedule s
        WHERE s.scheduleId = :scheduleId
    """)
    Long getDriverIdByScheduleId(Long scheduleId);
}
