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
public class DriverDTO {
    private Long driverId;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String imageUrl;
}

