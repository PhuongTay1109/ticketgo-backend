package com.ticketgo.dto;

import com.ticketgo.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LatestConversationMessagesDto {
    private String conversationId;
    private String title;
    private List<MessageDto> messages;

    @Data
    public static class MessageDto {
        private Role role;
        private String content;
        private LocalDateTime createdAt;
    }
}