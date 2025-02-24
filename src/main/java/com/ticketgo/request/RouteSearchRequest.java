package com.ticketgo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class RouteSearchRequest {
    @NotBlank(message =  "Nơi xuất phát không được để trống")
    private String departureLocation;
    @NotBlank(message =  "Nơi đến không được để trống")
    private String arrivalLocation;
    @NotNull(message =  "Ngày đi không được để trống")
    private LocalDate departureDate;
    private String sortBy;
    private String sortDirection;
    private int pageNumber;
    private int pageSize;
}

