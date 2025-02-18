package com.ticketgo.service;

import com.ticketgo.request.PaymentRequest;
import com.ticketgo.entity.Payment;

public interface PaymentService {
    String createVNPayment(PaymentRequest request);

    void save(Payment payment);
}
