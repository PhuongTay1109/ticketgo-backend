package com.ticketgo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "canceled_booking_histories")
public class CanceledBookingHistory extends BaseEntity {
    @Id
    private Long bookingId;
    private Long customerId;
    private LocalDateTime bookingDate;
    private String seatInfos;
    private String contactName;
    private String contactEmail;
    private String routeName;
    private LocalDateTime departureDate;
    private LocalDateTime pickupTime;
    private String pickupLocation;
    private String dropoffLocation;
    private String licensePlate;
    private Double originalPrice;
    private Double discountedPrice;
}
