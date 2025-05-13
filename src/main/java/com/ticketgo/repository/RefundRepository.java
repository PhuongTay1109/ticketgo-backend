package com.ticketgo.repository;

import com.ticketgo.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    @Query(value = """
            SELECT
                r
            FROM
                Refund r
            WHERE
                r.booking.bookingId = :bookingId
        """)
    Refund findByBookingId(Long bookingId);
}
