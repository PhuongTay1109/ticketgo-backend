package com.ticketgo.service;

public interface MessagingService {
    void send(String queueName, Object message);
}
