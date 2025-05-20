package com.ticketgo.request;

import lombok.Data;

@Data
public class AddRouteRequest {
    private Long routeId;
    private String routeName;
    private String departureLocation;
    private String arrivalLocation;
    private String routeImage;
}
