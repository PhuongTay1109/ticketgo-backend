package com.ticketgo.request;

import com.ticketgo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateRequestDto {
    private String conversationId;
    private Role role;
    private String content;
}
