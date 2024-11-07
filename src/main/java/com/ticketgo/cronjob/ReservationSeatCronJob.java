package com.ticketgo.cronjob;

import com.ticketgo.model.ReservationSeat;
import com.ticketgo.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Configuration
@EnableScheduling
public class ReservationSeatCronJob {
    private final ReservationSeatRepository reservationSeatRepo;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void releaseExpiredReservation() {
        log.info("Release Expired Reservation");
        List<ReservationSeat> expiredReservations = reservationSeatRepo.findAllByHoldUntilBefore(LocalDateTime.now());
        reservationSeatRepo.deleteAll(expiredReservations);
    }

}
