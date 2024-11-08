package com.ticketgo.repository;

import com.ticketgo.model.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE tickets t
            SET 
                t.status = 'REVERSED',
                t.reserved_until = CURRENT_TIMESTAMP + INTERVAL 5 MINUTE,
                t.customer_id = :customerId
            WHERE t.schedule_id = :scheduleId
            AND t.seat_id = :seatId
            """, nativeQuery = true)
    void reserveSeats(@Param("scheduleId") long scheduleId,
                      @Param("seatId") long seatId,
                      @Param("customerId") long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT t FROM Ticket t
        WHERE t.schedule.scheduleId = :scheduleId
        AND t.seat.seatId = :seatId 
        AND t.status = 'AVAILABLE'""")
    Ticket selectTicketForUpdate(@Param("scheduleId") long scheduleId, @Param("seatId") long seatId);


    List<Ticket> findAllByCustomer_UserId(long customerId);
}
