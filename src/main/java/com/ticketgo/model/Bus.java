package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "buses")
public class Bus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer busId;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String busType;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private LocalDate registrationExpiry;

    @Column(nullable = false)
    private LocalDate expirationDate;

//    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Seat> seats;
}

