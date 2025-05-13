package com.ticketgo.repository;

import com.ticketgo.entity.Ticket;
import com.ticketgo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE tickets t
            SET 
                t.status = 'RESERVED',
                t.reserved_until = CURRENT_TIMESTAMP + INTERVAL 5 MINUTE,
                t.customer_id = :customerId
            WHERE t.ticket_code = :ticketCode
            """, nativeQuery = true)
    void reserveSeats(@Param("ticketCode") String ticketCode,
                      @Param("customerId") long customerId);

    @Query("""
        SELECT t FROM Ticket t
        WHERE t.schedule.scheduleId = :scheduleId
        AND t.seat.seatId = :seatId 
        AND t.status = 'AVAILABLE'""")
    Ticket findTicketBySeatIdAndScheduleId(@Param("scheduleId") long scheduleId,
                                           @Param("seatId") long seatId);


    @Query("""
        SELECT t FROM Ticket t
        WHERE t.customer.userId = :customerId
        AND t.status = com.ticketgo.enums.TicketStatus.RESERVED
        """)
    List<Ticket> findReservedTicketsByCustomerId(long customerId);

    List<Ticket> findAllBySchedule_ScheduleId(long scheduleId);

    List<Ticket> findAllByBooking_BookingId(long bookingId);

    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM Ticket t
        WHERE t.customer = :customer
        AND t.status = com.ticketgo.enums.TicketStatus.RESERVED
    """)
    boolean existsReservedSeatsByCustomer(@Param("customer") User customer);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE tickets t
            SET 
                t.status = 'AVAILABLE',
                t.reserved_until = null,
                t.customer_id = null
            WHERE t.customer_id = :customerId
            AND t.schedule_id = :scheduleId
            AND t.status = 'RESERVED'
            """, nativeQuery = true)
    void releaseReservedSeatsByCustomer(
            @Param("customerId") long customerId,
            @Param("scheduleId") long scheduleId);

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0
            THEN false ELSE true END 
            FROM Ticket t WHERE t.seat.seatId = :seatId 
            AND t.schedule.scheduleId = :scheduleId 
            AND t.status = com.ticketgo.enums.TicketStatus.AVAILABLE
            """)
    boolean isSeatAvailable(@Param("seatId") long seatId, @Param("scheduleId") long scheduleId);

    @Query("""
        SELECT t.price FROM Ticket t
        WHERE t.schedule.scheduleId = :scheduleId
        AND t.seat.seatId = :seatId
        """)
    double getPriceBySeatIdAndScheduleId(@Param("scheduleId") long scheduleId,
                                         @Param("seatId") long seatId);

    Optional<Ticket> findByTicketCode(String ticketCode);
}
