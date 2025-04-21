package com.ticketgo.service.impl;

import com.ticketgo.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQService implements MessagingService {
    private final SimpMessagingTemplate template;

    @Override
    public void send(String destination, Object message) {
        template.convertAndSend(destination, message);
    }
}

