package com.ticketgo.repository;

import com.ticketgo.dto.BookingHistoryDTOTuple;
import com.ticketgo.dto.BookingInfoDTOTuple;
import com.ticketgo.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = """
        SELECT
            t.ticket_code AS ticketCode,
            b.contact_name AS contactName,
            b.contact_email AS contactEmail,
            r.route_name AS routeName,
            s.departure_time AS departureDate,
            ps.arrival_time AS pickupTime,
            ps.location AS pickupLocation,
            ds.location AS dropoffLocation,
            st.seat_number AS seatNumber,
            bs.license_plate AS licensePlate,
            b.original_price AS price
        FROM
            bookings b
        JOIN
            tickets t ON t.booking_id = b.booking_id
        JOIN
            schedules s ON t.schedule_id = s.schedule_id
        JOIN
            seats st ON t.seat_id = st.seat_id
        JOIN
            buses bs ON st.bus_id = bs.bus_id
        JOIN
            routes r on s.route_id = r.route_id
        JOIN
            route_stops ps ON b.pickup_stop_id = ps.stop_id
        JOIN
            route_stops ds ON b.dropoff_stop_id = ds.stop_id
        WHERE
            b.booking_id = :bookingId;
    """, nativeQuery = true)
    List<BookingInfoDTOTuple> findBookingInfoByBookingId(@Param("bookingId") Long bookingId);

    @Query(value = """
            SELECT
            t.ticket_code AS ticketCode,
            b.contact_name AS contactName,
            b.contact_email AS contactEmail,
            r.route_name AS routeName,
            s.departure_time AS departureDate,
            ps.arrival_time AS pickupTime,
            ps.location AS pickupLocation,
            ds.location AS dropoffLocation,
            st.seat_number AS seatNumber,
            bs.license_plate AS licensePlate,
            b.original_price AS price,
            b.status AS status
        FROM
            bookings b
        JOIN
            tickets t ON t.booking_id = b.booking_id
        JOIN
            schedules s ON t.schedule_id = s.schedule_id
        JOIN
            seats st ON t.seat_id = st.seat_id
        JOIN
            buses bs ON st.bus_id = bs.bus_id
        JOIN
            routes r ON s.route_id = r.route_id
        JOIN
            route_stops ps ON b.pickup_stop_id = ps.stop_id
        JOIN
            route_stops ds ON b.dropoff_stop_id = ds.stop_id
        WHERE
            b.status NOT LIKE "IN_PROGRESS"
        AND b.customer_id = :customerId
        ORDER BY
            b.booking_date DESC
        """, nativeQuery = true)
    Page<BookingHistoryDTOTuple> getBookingHistoryForCustomer(@Param("customerId") Long customerId, Pageable pageable);

}
