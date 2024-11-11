package com.ticketgo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticketgo.dto.request.BookingRequest;
import com.ticketgo.model.Payment;

public interface PaymentService {
    String createVNPayment(BookingRequest request) throws JsonProcessingException;

    void save(Payment payment);
}
