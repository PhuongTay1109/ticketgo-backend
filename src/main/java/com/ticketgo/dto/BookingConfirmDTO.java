package com.ticketgo.dto;

import com.ticketgo.response.PriceEstimationResponse;
import com.ticketgo.response.TripInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingConfirmDTO {
    private PriceEstimationResponse prices;
    private TripInformationResponse tripInformation;
}
