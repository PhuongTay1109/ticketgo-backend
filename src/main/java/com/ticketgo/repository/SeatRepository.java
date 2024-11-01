package com.ticketgo.repository;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.model.Bus;
import com.ticketgo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus(Bus bus);

    @Query("""
            SELECT COUNT(bs) FROM BookingSeat bs
            JOIN bs.booking b
            WHERE b.schedule.scheduleId = :scheduleId""")
    Integer countBookedSeatsForSchedule(@Param("scheduleId") Long scheduleId);

    @Query("""
       SELECT new com.ticketgo.dto.SeatStatusDTO(s.seatNumber,
               CASE WHEN bs.seat.seatId IS NOT NULL THEN TRUE ELSE FALSE END)
       FROM Seat s
       LEFT JOIN BookingSeat bs ON s.seatId = bs.seat.seatId
       LEFT JOIN Booking b ON bs.booking.bookingId = b.bookingId\s
       AND b.schedule.scheduleId = :scheduleId
      \s""")
    List<SeatStatusDTO> getSeatStatusForSchedule(@Param("scheduleId") Long scheduleId);

}
