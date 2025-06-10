package com.ticketgo.controller;

import com.ticketgo.dto.LatestConversationMessagesDto;
import com.ticketgo.entity.ChatMessage;
import com.ticketgo.entity.Conversation;
import com.ticketgo.repository.ConversationRepository;
import com.ticketgo.request.ChatRequest;
import com.ticketgo.service.ChatBotService;
import com.ticketgo.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
@Slf4j
public class ChatBotController {

    private final OpenAIService openAIService;
    private final ChatBotService chatBotService;
    private final ConversationRepository conversationRepository;

    @PostMapping("/conversations")
    public ResponseEntity<String> createConversation(@RequestParam(required = false) String userId) {
        String conversationId = chatBotService.createConversation(userId);
        return ResponseEntity.ok(conversationId);
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        try {
            String response = openAIService.chatWithToolCalls(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in chat: ", e);
            return ResponseEntity.internalServerError()
                    .body("Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại sau.");
        }
    }

    @GetMapping("/conversations/latest")
    public ResponseEntity<LatestConversationMessagesDto> getLatestConversationMessages(
            @RequestParam String userId) {

        Conversation conversation = conversationRepository
                .findTopByUserIdOrderByUpdatedAtDesc(userId)
                .orElseThrow(() -> new RuntimeException("User has no conversations"));

        List<ChatMessage> messages = chatBotService.getMessages(conversation.getConversationId());

        LatestConversationMessagesDto dto = new LatestConversationMessagesDto();
        dto.setConversationId(conversation.getConversationId());
        dto.setTitle(conversation.getTitle());

        dto.setMessages(messages.stream().map(msg -> {
            LatestConversationMessagesDto.MessageDto msgDto = new LatestConversationMessagesDto.MessageDto();
            msgDto.setRole(msg.getRole());
            msgDto.setContent(msg.getContent());
            msgDto.setCreatedAt(msg.getCreatedAt());
            return msgDto;
        }).toList());

        return ResponseEntity.ok(dto);
    }
}
