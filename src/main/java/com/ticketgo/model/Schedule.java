package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "schedules")
public class Schedule extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Id
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}
