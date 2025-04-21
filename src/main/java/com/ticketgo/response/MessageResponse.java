package com.ticketgo.response;

import java.time.LocalDateTime;

public class MessageResponse {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
}
