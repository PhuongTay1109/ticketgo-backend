package com.ticketgo.service;

import com.ticketgo.dto.RouteStopDTO;
import com.ticketgo.dto.response.RouteStopResponse;

import java.util.List;

public interface RouteStopService {
    RouteStopResponse getRouteStops(long scheduleId);

    List<RouteStopDTO> getPickupStops(long scheduleId);
    List<RouteStopDTO> getDropoffStops(long scheduleId);
}
