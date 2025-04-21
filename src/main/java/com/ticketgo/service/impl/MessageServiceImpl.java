package com.ticketgo.service.impl;

import com.ticketgo.constant.Topics;
import com.ticketgo.dto.MessageDTO;
import com.ticketgo.entity.Message;
import com.ticketgo.entity.User;
import com.ticketgo.repository.MessageRepository;
import com.ticketgo.repository.UserRepository;
import com.ticketgo.response.GetMessageResponse;
import com.ticketgo.response.MessageResponse;
import com.ticketgo.service.MessageService;
import com.ticketgo.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessagingService messagingService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public void sendMessage(MessageDTO messageDTO) {
        long senderId = messageDTO.getSenderId();
        long receiverId = messageDTO.getReceiverId();
        String content = messageDTO.getContent();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();
        messageRepository.save(message);
        messagingService.send(Topics.getChatTopic(receiverId), messageDTO);
    }

    @Override
    public GetMessageResponse getMessages(Long senderId, Long receiverId) {
        List<Message> messages = messageRepository.findAllBySenderIdAndReceiverId(senderId, receiverId);
        List<MessageResponse> messageResponses = messages.stream()
                .map(message -> MessageResponse.builder()
                        .messageId(message.getMessageId())
                        .senderId(message.getSender().getUserId())
                        .receiverId(message.getReceiver().getUserId())
                        .content(message.getContent())
                        .sentAt(message.getSentAt())
                        .build())
                .toList();
        return new GetMessageResponse(messageResponses);
    }
}
