package com.ticketgo.service;

public interface EmailService {
    void sendActivationEmail(String email, String token);
}
