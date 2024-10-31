package com.ticketgo.service;

import com.ticketgo.dto.response.RouteStopResponse;

public interface RouteStopService {
    RouteStopResponse getRouteStops(long scheduleId);
}
