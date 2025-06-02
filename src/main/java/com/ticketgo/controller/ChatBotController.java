package com.ticketgo.controller;

import com.ticketgo.request.ChatRequest;
import com.ticketgo.service.ChatBotService;
import com.ticketgo.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
@Slf4j
public class ChatBotController {

    private final OpenAIService openAIService;
    private final ChatBotService chatBotService;

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
}
