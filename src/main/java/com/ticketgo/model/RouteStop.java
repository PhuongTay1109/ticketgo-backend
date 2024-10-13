package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "route_stops")
public class RouteStop extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stopId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer stopOrder;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StopType stopType;
}

enum StopType {
    PICKUP,
    DROPOFF
}


