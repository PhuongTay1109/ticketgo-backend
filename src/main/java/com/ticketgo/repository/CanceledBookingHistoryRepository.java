package com.ticketgo.repository;

import com.ticketgo.entity.CanceledBookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanceledBookingHistoryRepository extends JpaRepository<CanceledBookingHistory, Long> {
    List<CanceledBookingHistory> findAllByCustomerId(Long userId);
}
