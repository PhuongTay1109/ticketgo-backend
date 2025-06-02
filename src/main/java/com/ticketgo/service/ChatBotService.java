package com.ticketgo.service;

import com.ticketgo.entity.ChatMessage;
import com.ticketgo.entity.Conversation;
import com.ticketgo.repository.ChatMessageRepository;
import com.ticketgo.repository.ConversationRepository;
import com.ticketgo.request.MessageCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatBotService {
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public String createConversation(String userId) {
        String conversationId = UUID.randomUUID().toString();

        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setTitle("Bus Route Search");

        conversationRepository.save(conversation);
        return conversationId;
    }

    @Transactional
    public void createMessage(MessageCreateRequestDto request) {
        Conversation conversation = conversationRepository
                .findByConversationId(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversation(conversation);
        chatMessage.setRole(request.getRole());
        chatMessage.setContent(request.getContent());

        chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(String conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}
