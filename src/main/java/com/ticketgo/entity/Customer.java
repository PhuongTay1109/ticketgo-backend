package com.ticketgo.entity;

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
@Table(name = "customers")
public class Customer extends User {
    @Column(nullable = false)
    private String fullName;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private Integer points;

    @Override
    public void prePersist() {
        super.prePersist();
        this.points = 0;
    }
}
