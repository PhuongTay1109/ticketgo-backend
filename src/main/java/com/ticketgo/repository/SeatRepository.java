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
                WHEN EXISTS (
                    SELECT 1
                    FROM booking_seats bs
                    WHERE bs.seat_id = s.seat_id
                    AND bs.booking_id IN (
                        SELECT booking_id
                        FROM bookings b
                        WHERE b.schedule_id = :scheduleId
                    )
                ) OR EXISTS (
                    SELECT 1
                    FROM reservation_seats rs
                    WHERE rs.seat_id = s.seat_id
                    AND rs.schedule_id = :scheduleId
                ) THEN TRUE
                ELSE FALSE
            END AS isBooked,
            s.seat_type AS seatType
        FROM
            seats s
        WHERE
            s.bus_id IN (
                SELECT bus_id
                FROM schedules
                WHERE schedule_id = :scheduleId
            )
        ORDER BY
            s.seat_number ASC
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
