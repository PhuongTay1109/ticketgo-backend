package com.ticketgo.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetMessageRequest {
    private Long senderId;
    private Long receiverId;
}
