package com.ticketgo.service.impl;

import com.ticketgo.request.BusListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.mapper.BusMapper;
import com.ticketgo.entity.Bus;
import com.ticketgo.repository.BusRepository;
import com.ticketgo.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {
    private final BusRepository busRepo;

    @Override
    public Bus findBySchedule(long scheduleId) {
        return busRepo.findBySchedule(scheduleId)
                .orElseThrow(() -> new RuntimeException(
                                "No bus found for schedule: " + scheduleId));
    }

    @Override
    public ApiPaginationResponse getAllBuses(BusListRequest req) {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        Page<Bus> busPage = busRepo.findAll(PageRequest.of(pageNumber - 1, pageSize));

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                busPage.getNumber() + 1,
                busPage.getSize(),
                busPage.getTotalPages(),
                busPage.getTotalElements()
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Danh sách xe",
                busPage.getContent().stream()
                        .map(BusMapper.INSTANCE::toBusDTO)
                        .collect(Collectors.toList()),
                pagination
        );
    }
}
