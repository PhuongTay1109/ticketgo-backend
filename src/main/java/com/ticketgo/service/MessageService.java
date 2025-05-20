package com.ticketgo.service;

import com.ticketgo.dto.ChatUserDTO;
import com.ticketgo.dto.MessageDTO;
import com.ticketgo.response.GetMessageResponse;

import java.util.List;

public interface MessageService {
    void sendMessage(MessageDTO messageDTO);

    GetMessageResponse getMessages(Long senderId, Long receiverId);

    List<ChatUserDTO> getChatUsers(Long myId);

    void markMessageAsRead(Long messageId);
}
