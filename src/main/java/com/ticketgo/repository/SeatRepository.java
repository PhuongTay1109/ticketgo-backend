package com.ticketgo.repository;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("""
            SELECT COUNT(bs)
            FROM BookingSeat bs
            JOIN bs.booking b
            WHERE b.schedule.scheduleId = :scheduleId""")
    Integer countBookedSeatsForSchedule(@Param("scheduleId") Long scheduleId);

    @Query("""
       SELECT DISTINCT
            new com.ticketgo.dto.SeatStatusDTO(
                s.seatNumber,
                CASE WHEN bs.seat.seatId IS NOT NULL
                    THEN TRUE ELSE FALSE END,
                s.seatType
            )
       FROM Seat s
       LEFT JOIN BookingSeat bs
            ON s.seatId = bs.seat.seatId
       LEFT JOIN Booking b
            ON bs.booking.bookingId = b.bookingId
            AND b.schedule.scheduleId = :scheduleId
       ORDER BY s.seatNumber ASC
      """)
    List<SeatStatusDTO> getSeatStatusForSchedule(@Param("scheduleId") Long scheduleId);

}
