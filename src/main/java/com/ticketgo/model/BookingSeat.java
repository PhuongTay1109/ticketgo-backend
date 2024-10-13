package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "booking_seats")
public class BookingSeat extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Id
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
}
