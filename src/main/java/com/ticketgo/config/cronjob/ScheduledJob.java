package com.ticketgo.config.cronjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class ScheduledJob {
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    @Async("scheduledJobExecutor")
    public void resetBookingStatus() {
        String sql = """
                UPDATE bookings SET status = 'FAILED'
                WHERE TIMESTAMPDIFF(MINUTE, booking_date, NOW()) >= 15 
                AND status = 'IN_PROGRESS'
        """;
        jdbcTemplate.update(sql);
        log.info("Reset booking status executed.");
    }

    @Scheduled(fixedRate = 10000)
    @Async("scheduledJobExecutor")
    public void resetTicketInfo() {
        String sql = """
                UPDATE tickets 
                SET status = 'AVAILABLE', 
                    booking_id = NULL, 
                    customer_id = NULL 
                WHERE booking_id IN ( 
                    SELECT booking_id FROM bookings
                    WHERE TIMESTAMPDIFF(MINUTE, booking_date, NOW()) >= 15 
                    AND status = 'IN_PROGRESS'
                )
        """;
        jdbcTemplate.update(sql);
        log.info("Reset ticket info executed.");
    }

    @Scheduled(fixedRate = 1000)
    @Async("scheduledJobExecutor")
    public void resetStatusTickets() {
        String sql = """
                UPDATE tickets 
                SET status = 'AVAILABLE', 
                    reserved_until = NULL, 
                    customer_id = NULL
                WHERE reserved_until < NOW()
        """;
        jdbcTemplate.update(sql);
        log.info("Reset status tickets executed.");
    }
}
