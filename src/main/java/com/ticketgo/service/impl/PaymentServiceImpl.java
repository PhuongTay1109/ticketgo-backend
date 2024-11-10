package com.ticketgo.service.impl;

import com.ticketgo.model.Payment;
import com.ticketgo.repository.PaymentRepository;
import com.ticketgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;

    @Override
    public void save(Payment payment) {
        paymentRepo.save(payment);
    }
}
