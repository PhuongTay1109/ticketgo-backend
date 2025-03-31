package com.ticketgo.constant;

public enum BookingStep {
    SELECT_SEAT(1),
    HOLD_TICKET(2),
    PAYMENT(3);

    private final int step;

    BookingStep(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }
}
