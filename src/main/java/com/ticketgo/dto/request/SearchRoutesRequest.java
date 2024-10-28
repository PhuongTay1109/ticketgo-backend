package com.ticketgo.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SearchRoutesRequest {
    private String departureLocation;
    private String arrivalLocation;
    private LocalDate departureDate;
    private String sortBy;
    private String sortDirection;
    private int pageNumber;
    private int pageSize;
}

