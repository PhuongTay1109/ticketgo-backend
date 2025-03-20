package com.ticketgo.repository;

import com.ticketgo.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {
    @Query("""
        SELECT s.driver.driverId
        FROM Schedule s
        WHERE s.scheduleId = :scheduleId
    """)
    Long getDriverIdByScheduleId(Long scheduleId);

@Query("SELECT s FROM Schedule s WHERE s.bus.busId IN :busIds " +
            "AND s.departureTime < :startOfDay " +
            "ORDER BY s.departureTime DESC")
    Schedule findLatestPreviousDaySchedule(@Param("busIds") List<Long> busIds, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT s FROM Schedule s WHERE s.bus.busId IN :busIds " +
            "AND s.departureTime > :endOfDay " +
            "ORDER BY s.departureTime ASC")
    Schedule findEarliestNextDaySchedule(@Param("busIds") List<Long> busIds, @Param("endOfDay") LocalDateTime endOfDay);
}
