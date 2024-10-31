package com.ticketgo.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookingSeatId implements Serializable {
    private Long bookingId;
    private Long seatId;

    public BookingSeatId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingSeatId that)) return false;
        return bookingId.equals(that.bookingId) && seatId.equals(that.seatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, seatId);
    }
}
