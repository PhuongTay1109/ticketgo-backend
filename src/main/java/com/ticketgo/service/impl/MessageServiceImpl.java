package com.ticketgo.service.impl;

import com.ticketgo.constant.Topics;
import com.ticketgo.dto.ChatUserDTO;
import com.ticketgo.dto.MessageDTO;
import com.ticketgo.entity.BusCompany;
import com.ticketgo.entity.Customer;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
                        .isRead(message.isRead())
                        .readAt(message.getReadAt())
                        .build())
                .toList();
        return new GetMessageResponse(messageResponses);
    }

    public List<ChatUserDTO> getChatUsers(Long myId) {
        List<Long> partnerIds = messageRepository.findChatPartnerIds(myId);

        List<ChatUserDTO> chatUsers = new ArrayList<>();
        for (Long partnerId : partnerIds) {
            User user = userRepository.findById(partnerId).orElse(null);
            if (user == null) continue;

            Message lastMessage = messageRepository
                    .findConversationMessages(myId, partnerId)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (lastMessage == null) continue;

            String name = "";
            String avt = "";
            if (user instanceof Customer customer) {
                name = customer.getFullName();
                avt = customer.getImageUrl();
            } else if (user instanceof BusCompany busCompany) {
                name = busCompany.getBusCompanyName();
                avt = "https://res.cloudinary.com/dj1h07rea/image/upload/v1746855511/avt_cojdbt.jpg";
            }

            ChatUserDTO cu = new ChatUserDTO();
            cu.setUserId(partnerId);
            cu.setName(name);
            cu.setAvatar(avt);
            cu.setLastMessage(lastMessage.getContent());
            cu.setLastMessageTime(lastMessage.getSentAt().toString());
            cu.setIsRead(lastMessage.isRead());
            cu.setReadAt(lastMessage.getReadAt());

            chatUsers.add(cu);
        }

        chatUsers.sort(Comparator.comparing(ChatUserDTO::getLastMessageTime).reversed());

        return chatUsers;
    }

    public void markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));

        if (!message.isRead()) {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

}
