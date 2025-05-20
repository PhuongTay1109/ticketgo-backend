package com.ticketgo.controller;

import com.ticketgo.dto.MessageDTO;
import com.ticketgo.entity.User;
import com.ticketgo.request.GetMessageRequest;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody MessageDTO messageDTO) {
        messageService.sendMessage(messageDTO);
        return ResponseEntity.ok(null);
    }

    @GetMapping()
    public ApiResponse findChatMessages(@ModelAttribute GetMessageRequest request) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy danh sách tin nhắn thành công",
                messageService.getMessages(request.getSenderId(), request.getReceiverId())
        );
    }

    @GetMapping("/chat-users")
    public ApiResponse findChatUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return new ApiResponse(
                HttpStatus.OK,
                "Lấy danh sách chat users thành công",
                messageService.getChatUsers(user.getUserId())
        );
    }

    @PutMapping("/mark-as-read/{messageId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

}

