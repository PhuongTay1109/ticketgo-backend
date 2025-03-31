package com.ticketgo.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeys {
    public static String tokenKey(String tokenType) {
        return "token:" + tokenType;
    }

    public static String blackListTokenKey = "blacklist:token";

    public static String blackListUserKey = "blacklist:user";

    public static String userBookingInfoKey(Long userId, Long scheduleId) {
        return "bookingInfo:" + userId + ":" + scheduleId;
    }

    public static String vnPayUrlKey(Long userId, Long scheduleId) {
        return "vnPayBooking:" + userId + ":" + scheduleId;
    }
}
