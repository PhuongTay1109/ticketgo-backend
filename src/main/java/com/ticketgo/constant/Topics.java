package com.ticketgo.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Topics {
    public static final String CHAT_TOPIC = "/topic/chat-{userId}";

    public static final String USER_ID_PLACEHOLDER = "{userId}";

   public static String getChatTopic(long userId) {
        return CHAT_TOPIC.replace(USER_ID_PLACEHOLDER, String.valueOf(userId));
   }
}
