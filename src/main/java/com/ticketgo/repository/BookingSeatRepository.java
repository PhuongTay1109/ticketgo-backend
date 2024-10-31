package com.ticketgo.repository;

import com.ticketgo.model.Booking;
import com.ticketgo.model.BookingSeat;
import com.ticketgo.model.BookingSeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {
    BookingSeat findByBooking(Booking bookings);
}
