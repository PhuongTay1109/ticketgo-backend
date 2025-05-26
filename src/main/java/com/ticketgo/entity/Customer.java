package com.ticketgo.entity;

import com.ticketgo.enums.MembershipLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@ToString
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

    @Enumerated(EnumType.STRING)
    private MembershipLevel level;

    @Override
    public void prePersist() {
        super.prePersist();
        this.points = 0;
    }
}
