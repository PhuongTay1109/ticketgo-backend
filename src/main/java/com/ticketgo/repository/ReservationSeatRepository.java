package com.ticketgo.repository;

import com.ticketgo.model.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findAllByHoldUntilBefore(LocalDateTime time);

    @Query("""
        SELECT rs
        FROM ReservationSeat rs
        Where rs.schedule.scheduleId = :scheduleId
        """)
    List<ReservationSeat> findAllByScheduleId(long scheduleId);
}
