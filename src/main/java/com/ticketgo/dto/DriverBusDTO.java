package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverBusDTO {
    private DriverDTO driver;
    private BusDTO bus;
}
