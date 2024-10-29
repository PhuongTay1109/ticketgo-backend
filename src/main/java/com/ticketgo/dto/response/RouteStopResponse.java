package com.ticketgo.dto.response;

import com.ticketgo.dto.RouteStopDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteStopResponse {
    private List<RouteStopDTO> pickup;
    private List<RouteStopDTO> dropoff;
}
