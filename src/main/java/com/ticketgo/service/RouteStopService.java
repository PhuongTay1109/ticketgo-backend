package com.ticketgo.service;

import com.ticketgo.dto.RouteStopDTO;
import com.ticketgo.response.RouteStopResponse;
import com.ticketgo.model.RouteStop;

import java.util.List;

public interface RouteStopService {
    RouteStopResponse getRouteStops(long scheduleId);

    List<RouteStopDTO> getPickupStops(long scheduleId);
    List<RouteStopDTO> getDropoffStops(long scheduleId);

    RouteStop findById(long id);
}
