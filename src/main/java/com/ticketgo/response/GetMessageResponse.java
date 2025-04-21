package com.ticketgo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetMessageResponse {
    List<MessageResponse> messages;
}
