package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @Column(nullable = false)
    private String bookingCode;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private Double originalAmount;

    @Column(nullable = false)
    private Double discountedAmount;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(nullable = false)
    private String passengerName;

    @Column(nullable = false)
    private String passengerEmail;

    @Column(nullable = false)
    private String passengerPhone;

    @Column(nullable = false)
    private LocalDate passengerDateOfBirth;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

enum BookingStatus {
    CONFIRMED,
    COMPLETED,
    CANCELLED,
    REFUNDED
}



