package com.ticketgo.service.impl;

import com.ticketgo.model.Route;
import com.ticketgo.model.Schedule;
import com.ticketgo.repository.RouteRepository;
import com.ticketgo.repository.specification.ScheduleSpecification;
import com.ticketgo.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;

}
