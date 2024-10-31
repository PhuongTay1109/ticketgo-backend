package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking_seats")
public class BookingSeat extends BaseEntity {

    @EmbeddedId
    private BookingSeatId id;

    @ManyToOne
    @MapsId("bookingId") // Maps the "booking_id" field in the composite key
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @MapsId("seatId") // Maps the "seat_id" field in the composite key
    @JoinColumn(name = "seat_id")
    private Seat seat;
}