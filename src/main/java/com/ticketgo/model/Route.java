package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "routes")
public class Route extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @Column(nullable = false)
    private String routeName;

    @Column(nullable = false)
    private String departureLocation;

    @Column(nullable = false)
    private String arrivalLocation;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Schedule> schedules;
}

