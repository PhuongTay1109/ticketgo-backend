package com.ticketgo.repository;

import com.ticketgo.model.Booking;
import com.ticketgo.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(bs) FROM BookingSeat bs " +
            "JOIN bs.booking b " +
            "WHERE b.schedule.scheduleId = :scheduleId")
    Integer countBookedSeatsForSchedule(@Param("scheduleId") Long scheduleId);
}
