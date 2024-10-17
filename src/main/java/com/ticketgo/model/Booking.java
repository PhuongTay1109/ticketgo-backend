package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

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

    private String passengerName;

    private String passengerEmail;

    private String passengerPhone;

    private LocalDate passengerDateOfBirth;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}



