package com.ticketgo.service;

import com.ticketgo.entity.Driver;
import com.ticketgo.entity.Schedule;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendActivationEmail(String email, String token);
    void sendBookingInfo(long bookingId, long scheduleId);
    void sendResetPasswordEmail(String email, String token);

    void sendBookingInfoReturn(Long bookingId, Long scheduleId, Long returnBookingId, Long returnScheduleId);

    void sendUpdateDriver(Schedule schedule, long bookingId, Driver driver);
}
