package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusDTO {
    private Long busId;
    private String licensePlate;
    private String busType;
    private String busImage;
    private Integer totalSeats;
    private Integer floors;
    private String registrationExpiry;
    private String expirationDate;
}

