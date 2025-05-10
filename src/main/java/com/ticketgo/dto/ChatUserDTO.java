package com.ticketgo.dto;

import lombok.Data;

@Data
public class ChatUserDTO {
    private Long userId;
    private String name;
    private String avatar;
    private String lastMessage;
    private String lastMessageTime;
}
