package com.ticketgo.enums;

public enum MembershipLevel {
    NEW_PASSENGER("Hành Khách Mới"),
    LOYAL_TRAVELER("Lữ Khách Thân Thiết"),
    GOLD_COMPANION("Đồng Hành Vàng"),
    ELITE_EXPLORER("Nhà Du Hành Ưu Tú");

    private final String vietnameseName;

    MembershipLevel(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}

