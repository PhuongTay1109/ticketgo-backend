package com.ticketgo.repository;

import com.ticketgo.dto.SeatStatusDTOTuple;
import com.ticketgo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("""
            SELECT COUNT(bs)
            FROM BookingSeat bs
            JOIN bs.booking b
            WHERE b.schedule.scheduleId = :scheduleId
            """)
    Integer countBookedSeatsForSchedule(@Param("scheduleId") Long scheduleId);

    @Query(value = """
        SELECT DISTINCT
            s.seat_id AS seatId,
            s.seat_number AS seatNumber,
            CASE
                WHEN bs.seat_id IS NOT NULL THEN TRUE
                ELSE FALSE
            END AS isBooked,
            s.seat_type AS seatType
        FROM
            seats s
        LEFT JOIN
            booking_seats bs ON s.seat_id = bs.seat_id
        LEFT JOIN
            bookings b ON bs.booking_id = b.booking_id AND b.schedule_id = 1
        LEFT JOIN
            buses bus ON s.bus_id = bus.bus_id
        WHERE
            bus.bus_id IN (
                SELECT bus_id FROM schedules WHERE schedule_id = 1
            )
        ORDER BY\s
            s.seat_number ASC;
    """, nativeQuery = true)
    List<SeatStatusDTOTuple> findSeatStatusByScheduleId(@Param("scheduleId") Long scheduleId);


    @Query("""
            SELECT s
            FROM Seat s
            JOIN BookingSeat bs ON s.seatId = bs.seat.seatId
            JOIN Booking b ON bs.booking.bookingId = b.bookingId
            WHERE b.schedule.scheduleId = :scheduleId
        """)
    Set<Seat> getBookedSeatsForSchedule(@Param("scheduleId") Long scheduleId);


}
