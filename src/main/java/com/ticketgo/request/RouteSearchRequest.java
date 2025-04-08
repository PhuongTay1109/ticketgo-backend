package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class RouteSearchRequest {
    private String departureLocation;
    private String arrivalLocation;
    private LocalDate departureDate;
    private String sortBy;
    private String sortDirection;
    private int pageNumber;
    private int pageSize;
}

