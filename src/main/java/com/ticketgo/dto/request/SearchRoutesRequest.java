package com.ticketgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SearchRoutesRequest {
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

