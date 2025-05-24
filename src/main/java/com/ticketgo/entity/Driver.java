package com.ticketgo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.Set;

@ToString
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "drivers")
@SQLRestriction("is_deleted = false")
public class Driver extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String placeOfIssue;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Schedule> schedules;
}
