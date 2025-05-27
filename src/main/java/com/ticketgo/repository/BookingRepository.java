package com.ticketgo.repository;

import com.ticketgo.entity.Booking;
import com.ticketgo.enums.BookingStatus;
import com.ticketgo.mapper.BusTypeStatisticsTuple;
import com.ticketgo.mapper.CustomerStatisticsTuple;
import com.ticketgo.mapper.OverallStatsTuple;
import com.ticketgo.mapper.RouteStatisticsTuple;
import com.ticketgo.projector.BookingHistoryDTOTuple;
import com.ticketgo.projector.BookingInfoDTOTuple;
import com.ticketgo.projector.CustomerInfoDTOTuple;
import com.ticketgo.projector.RevenueStatisticsDTOTuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = """
        SELECT
            b.booking_id AS bookingId,
            b.booking_date as bookingDate,
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
                b.booking_id AS bookingId,
                b.booking_date as bookingDate,
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
            b.original_price AS originalPrice,
            b.discounted_price AS discountedPrice,
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
        AND  b.status NOT LIKE "FAILED"
        AND b.customer_id = :customerId
        ORDER BY
            b.booking_date DESC
        """, nativeQuery = true)
    Page<BookingHistoryDTOTuple> getBookingHistoryForCustomer(@Param("customerId") Long customerId, Pageable pageable);


    @Query(value = "SELECT " +
            "seats.seat_number AS seatNumber, " +
            "b.contact_phone AS customerPhone, " +
            "b.contact_name AS customerName, " +
            "pickup_stop.location AS pickupLocation, " +
            "dropoff_stop.location AS dropoffLocation " +
            "FROM seats " +
            "INNER JOIN buses bus ON seats.bus_id = bus.bus_id " +
            "INNER JOIN schedules s ON s.bus_id = bus.bus_id " +
            "LEFT JOIN tickets t ON t.seat_id = seats.seat_id AND t.schedule_id = :scheduleId " +
            "LEFT JOIN bookings b ON b.booking_id = t.booking_id " +
            "LEFT JOIN route_stops pickup_stop ON pickup_stop.stop_id = b.pickup_stop_id " +
            "LEFT JOIN route_stops dropoff_stop ON dropoff_stop.stop_id = b.dropoff_stop_id " +
            "WHERE s.schedule_id = :scheduleId " +
            "ORDER BY seats.seat_number",
            nativeQuery = true)
    List<Object[]> findPassengerInfoByScheduleIdNative(@Param("scheduleId") Long scheduleId);


    // Phương thức chuyển đổi kết quả thành DTO
    default List<CustomerInfoDTOTuple> getPassengerInfoByScheduleId(Long scheduleId) {
        return findPassengerInfoByScheduleIdNative(scheduleId).stream()
                .map(result -> new CustomerInfoDTOTuple(
                        (String) result[0],
                        (String) result[1],
                        (String) result[2],
                        (String) result[3],
                        (String) result[4]))
                .toList();
    }

    @Modifying
    @Transactional
    @Query("""
        UPDATE Booking b
        SET b.status = :status
        WHERE b.bookingId = :bookingId
    """)
    void updateBookingStatusByBookingId(BookingStatus status, Long bookingId);

    @Query(value = """
        SELECT
            b.booking_id AS bookingId,
            b.booking_date as bookingDate,
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
            b.original_price AS originalPrice,
            b.discounted_price AS discountedPrice,
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
            b.status NOT LIKE 'IN_PROGRESS'
        AND  b.status NOT LIKE 'FAILED'
        AND b.customer_id IN :customerIds
        ORDER BY
            b.booking_date DESC
        """, nativeQuery = true)
    List<BookingHistoryDTOTuple> getAllBookingHistory(@Param("customerIds") List<Long> customerIds);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Booking b
        SET b.status = :bookingStatus
        WHERE b.bookingId IN (
            SELECT t.booking.bookingId
            FROM Ticket t
            WHERE t.schedule.scheduleId = :scheduleId
        )
    """)
    void updateStatusByScheduleId(@Param("scheduleId") Long scheduleId, @Param("bookingStatus") BookingStatus bookingStatus);

    // New queries
    @Query(value = """
    SELECT DATE_FORMAT(b.booking_date, :dateFormat) AS period,
            SUM(COALESCE(b.discounted_price, b.original_price)) AS totalRevenue,
            SUM(ticket_count) AS totalTicketsSold
     FROM (
         SELECT b.booking_id, b.booking_date, b.discounted_price, b.original_price,
                (SELECT COUNT(*) FROM tickets t WHERE t.booking_id = b.booking_id) AS ticket_count
         FROM bookings b
         WHERE (b.status = 'COMPLETED' OR b.status = 'CONFIRMED')
           AND b.booking_date BETWEEN :startDate AND :endDate
     ) b
     GROUP BY DATE_FORMAT(b.booking_date, :dateFormat)
                                                     
""", nativeQuery = true)
    List<RevenueStatisticsDTOTuple> getRevenueStatistics(
            @Param("dateFormat") String dateFormat,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Query(value = """
        SELECT r.route_name AS routeName,
               SUM(COALESCE(b.discounted_price, b.original_price)) AS totalRevenue,
               COUNT(b.booking_id) AS totalBookings,
               COUNT(DISTINCT b.customer_id) AS uniqueCustomers
        FROM bookings b
        JOIN route_stops rs_pickup ON b.pickup_stop_id = rs_pickup.stop_id
        JOIN schedules s ON rs_pickup.schedule_id = s.schedule_id
        JOIN routes r ON s.route_id = r.route_id
        WHERE (b.status = 'COMPLETED' OR b.status = 'CONFIRMED')
          AND b.booking_date BETWEEN :startDate AND :endDate
        GROUP BY r.route_name
    """, nativeQuery = true)
    List<RouteStatisticsTuple> getRouteStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT bt.bus_type AS busType,
               SUM(COALESCE(b.discounted_price, b.original_price)) AS totalRevenue,
               COUNT(b.booking_id) AS totalBookings,
               (COUNT(t.ticket_code) * 100.0 / bt.total_seats) AS averageOccupancyRate
        FROM bookings b
        JOIN tickets t ON b.booking_id = t.booking_id
        JOIN schedules s ON t.schedule_id = s.schedule_id
        JOIN buses bt ON s.bus_id = bt.bus_id
        WHERE (b.status = 'COMPLETED' OR b.status = 'CONFIRMED')
          AND b.booking_date BETWEEN :startDate AND :endDate
        GROUP BY bt.bus_type, bt.total_seats
    """, nativeQuery = true)
    List<BusTypeStatisticsTuple> getBusTypeStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT 
            COUNT(DISTINCT CASE WHEN u.created_at BETWEEN :startDate AND :endDate THEN u.user_id END) AS newCustomers,
            COUNT(DISTINCT CASE WHEN u.created_at < :startDate AND b.booking_date BETWEEN :startDate AND :endDate THEN u.user_id END) AS returningCustomers,
            COUNT(b.booking_id) * 1.0 / COUNT(DISTINCT b.customer_id) AS averageBookingsPerCustomer
        FROM users u
        LEFT JOIN bookings b ON u.user_id = b.customer_id
            AND (b.status = 'COMPLETED' OR b.status = 'CONFIRMED')
            AND b.booking_date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
        CustomerStatisticsTuple getCustomerStatistics(
                @Param("startDate") LocalDateTime startDate,
                @Param("endDate") LocalDateTime endDate);


    @Query(value = """
    SELECT
        SUM(COALESCE(b.discounted_price, b.original_price)) AS totalRevenue,
        COUNT(b.booking_id) AS totalBookings,
        (SELECT COUNT(*) FROM canceled_booking_histories
         WHERE booking_date BETWEEN :startDate AND :endDate) AS totalCancellations,
        CASE WHEN COUNT(b.booking_id) > 0
             THEN SUM(COALESCE(b.discounted_price, b.original_price)) / COUNT(b.booking_id)
             ELSE 0 END AS averageTicketPrice,
        (SELECT COUNT(*) FROM tickets t
         JOIN bookings b2 ON t.booking_id = b2.booking_id
         WHERE b2.status IN ('COMPLETED', 'CONFIRMED')
           AND b2.booking_date BETWEEN :startDate AND :endDate) AS totalTicketsSold
    FROM bookings b
    WHERE b.status IN ('COMPLETED', 'CONFIRMED')
      AND b.booking_date BETWEEN :startDate AND :endDate
    
    """, nativeQuery = true)
    OverallStatsTuple getOverallStats(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
