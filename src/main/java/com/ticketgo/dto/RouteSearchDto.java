package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteSearchDto {
    private String departureLocation;
    private String arrivalLocation;
    private String departureDate; // yyyy-MM-dd format
    private String sortBy;
    private String sortDirection;
    private Integer pageNumber;
    private Integer pageSize;
}