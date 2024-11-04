package com.ticketgo.repository;

import com.ticketgo.dto.SeatPriceDTO;
import com.ticketgo.model.SeatPricing;
import com.ticketgo.model.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatPricingRepository extends JpaRepository<SeatPricing, Long> {
    @Query(""" 
        SELECT COALESCE(sp.price, 0.0)
        FROM SeatPricing sp
        WHERE sp.schedule.scheduleId = :scheduleId
        AND sp.seatType = :seatType
        """)
    Double findPriceByScheduleIdAndSeatType(@Param("scheduleId") long scheduleId,
                                            @Param("seatType") SeatType seatType);

    @Query("""
            SELECT
                new com.ticketgo.dto.SeatPriceDTO(
                    sp.seatType, 
                    sp.price
                )
            FROM SeatPricing sp
            WHERE sp.schedule.scheduleId = :scheduleId
            """)
    List<SeatPriceDTO> getSeatPricesByScheduleId(@Param("scheduleId") long scheduleId);

}

