package com.ticketgo.repository;

import com.ticketgo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
//    @Query("""
//           SELECT new com.ticketgo.dto.SeatStatusDTO(
//               s.seatId,
//               s.seatNumber,
//               CASE WHEN
//                    t.status IN (
//                        com.ticketgo.model.TicketStatus.BOOKED,
//                        com.ticketgo.model.TicketStatus.RESERVED
//                    )
//                    THEN true
//                    ELSE false END,
//               s.seatType)
//           FROM Ticket t
//           LEFT JOIN t.seat s
//           WHERE t.schedule.scheduleId = :scheduleId
//           ORDER BY s.seatNumber ASC
//           """)
//    List<SeatStatusDTO> findSeatStatusesByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("""
           SELECT COUNT(t)
           FROM Ticket t
           WHERE t.schedule.scheduleId = :scheduleId
           AND t.status = com.ticketgo.model.TicketStatus.AVAILABLE
           """)
    int countAvailableSeatsByScheduleId(@Param("scheduleId") Long scheduleId);
}
