package com.ticketgo.service.impl;

import com.ticketgo.dto.BusDTO;
import com.ticketgo.entity.Bus;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.BusMapper;
import com.ticketgo.repository.BusRepository;
import com.ticketgo.request.BusListRequest;
import com.ticketgo.request.BusUpdateRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.BusService;
import com.ticketgo.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusServiceImpl implements BusService {
    private final BusRepository busRepo;

    @Override
    @Transactional
    public Bus findBySchedule(long scheduleId) {
        return busRepo.findBySchedule(scheduleId)
                .orElseThrow(() -> {
                    log.error("No bus found for schedule ID: {}", scheduleId);
                    return new AppException("Không tìm thấy thông tin xe", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    @Transactional
    public ApiPaginationResponse getBuses(BusListRequest req) {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, req.buildSort());
        Page<Bus> busPage = busRepo.findAll(pageable);

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

    @Override
    @Transactional
    public void createBus(BusDTO dto) {
        busRepo.findByLicensePlate(dto.getLicensePlate())
                .ifPresent(bus -> {
                    throw new AppException("Xe với biển số xe này đã tồn tại", HttpStatus.BAD_REQUEST);
                });

        Bus savedBus = BusMapper.INSTANCE.toBus(dto);
        busRepo.save(savedBus);
    }

    @Override
    @Transactional
    public BusDTO getBusById(Long id) {
        return busRepo.findByBusId(id)
                .map(BusMapper.INSTANCE::toBusDTO)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin xe", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public void updateBus(Long id, BusUpdateRequest req) {
        Bus bus = busRepo.findByBusId(id)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin xe", HttpStatus.NOT_FOUND));
        ObjectUtils.copyProperties(req, bus);
        busRepo.save(bus);
    }

    @Override
    @Transactional
    public void deleteBus(Long id) {
        busRepo.softDelete(id);
    }
}
