package com.ticketgo.service;

import com.ticketgo.dto.MessageDTO;
import com.ticketgo.response.GetMessageResponse;

public interface MessageService {
    void sendMessage(MessageDTO messageDTO);

    GetMessageResponse getMessages(Long senderId, Long receiverId);
}
