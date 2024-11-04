package com.ticketgo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_pricings", uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "seat_type_id"}))
public class SeatPricing extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatPricingId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(nullable = false)
    private Double price;
}

