package com.ticketgo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatUserDTO {
    private Long userId;
    private String name;
    private String avatar;
    private String lastMessage;
    private String lastMessageTime;
    private Boolean isRead;
    private LocalDateTime readAt;
}
