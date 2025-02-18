package com.ticketgo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

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
    private Long busId;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private String busType;

    @Column(nullable = false)
    private String busImage;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer floors;

    @Column(nullable = false)
    private LocalDate registrationExpiry;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Seat> seats;
}
