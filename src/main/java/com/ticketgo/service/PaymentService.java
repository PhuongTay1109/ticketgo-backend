package com.ticketgo.service;

import com.ticketgo.dto.request.PaymentRequest;
import com.ticketgo.model.Payment;

public interface PaymentService {
    String createVNPayment(PaymentRequest request);

    void save(Payment payment);
}
