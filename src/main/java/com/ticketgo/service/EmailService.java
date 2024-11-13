package com.ticketgo.service;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendActivationEmail(String email, String token);
    void sendBookingInfo(long bookingId);
}
