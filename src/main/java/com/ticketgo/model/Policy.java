package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "policies")
@Entity
public class Policy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_company_id", nullable = false)
    private BusCompany busCompany;

    @Column(nullable = false)
    private String policyType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String policyContent;
}

