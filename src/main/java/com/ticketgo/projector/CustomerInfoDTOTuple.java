package com.ticketgo.projector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoDTOTuple {
    private String seatNumber;
    private String customerPhone;
    private String customerName;
    private String pickupLocation;
    private String dropoffLocation;
}
