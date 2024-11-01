package com.ticketgo.repository;

import com.ticketgo.model.Booking;
import com.ticketgo.model.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBySchedule(Schedule schedule);
}
