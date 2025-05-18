package com.ticketgo.enums;

public enum ScheduleStatus {
    SCHEDULED("Chưa khởi hành"),
    IN_PROGRESS("Đang chạy"),
    COMPLETED("Hoàn thành");

    private final String displayName;

    ScheduleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
