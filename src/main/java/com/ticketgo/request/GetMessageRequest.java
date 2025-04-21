package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetMessageRequest {
    private Long senderId;
    private Long receiverId;
}
