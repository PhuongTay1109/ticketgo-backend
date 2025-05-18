package com.ticketgo.repository;

import com.ticketgo.entity.CanceledBookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanceledBookingHistoryRepository extends JpaRepository<CanceledBookingHistory, Long> {
    List<CanceledBookingHistory> findAllByCustomerId(Long userId);

    @Query("""
        Select c
        from CanceledBookingHistory c
        where c.customerId in :customerIds
    """)
    List<CanceledBookingHistory> findAllCustomer(List<Long> customerIds);
}
