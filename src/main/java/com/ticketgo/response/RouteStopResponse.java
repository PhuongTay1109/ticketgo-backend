package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketgo.dto.RouteStopDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteStopResponse {
    private List<RouteStopDTO> pickup;
    private List<RouteStopDTO> dropoff;
}
