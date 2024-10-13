package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seats")
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seatId;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private Boolean isBooked;
}
