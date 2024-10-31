package com.ticketgo.repository;

import com.ticketgo.model.Bus;
import com.ticketgo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus(Bus bus);
}
