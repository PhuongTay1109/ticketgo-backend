package com.ticketgo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Seat> seats;

    @Override
    public void prePersist() {
        super.prePersist();
        if (seats == null || seats.isEmpty()) {
            seats = new ArrayList<>();
            int seatsPerFloor = totalSeats / floors;

            for (int floor = 1; floor <= floors; floor++) {
                for (int i = 1; i <= seatsPerFloor; i++) {
                    Seat seat = Seat.builder()
                            .bus(this)
                            .seatNumber(floor + "-" + i)
                            .floor(floor)
                            .build();
                    seats.add(seat);
                }
            }
        }
    }
}

