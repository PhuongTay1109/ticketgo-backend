package com.ticketgo.service.impl;

import com.ticketgo.dto.DriverBusDTO;
import com.ticketgo.dto.DriverDTO;
import com.ticketgo.entity.Bus;
import com.ticketgo.entity.Driver;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.BusMapper;
import com.ticketgo.mapper.DriverMapper;
import com.ticketgo.repository.BusRepository;
import com.ticketgo.repository.DriverRepository;
import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.request.DriverCreateRequest;
import com.ticketgo.request.DriverListRequest;
import com.ticketgo.request.DriverUpdateRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.DriverService;
import com.ticketgo.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepo;
    private final ScheduleRepository scheduleRepo;
    private final BusRepository busRepository;

    @Override
    @Transactional
    public ApiPaginationResponse list(DriverListRequest req) {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, req.buildSort());
        Page<Driver> driverPage = driverRepo.findAll(pageable);

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                driverPage.getNumber() + 1,
                driverPage.getSize(),
                driverPage.getTotalPages(),
                driverPage.getTotalElements()
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Danh sách tài xế",
                driverPage.getContent().stream()
                        .map(DriverMapper.INSTANCE::fromEntityToDTO)
                        .collect(Collectors.toList()),
                pagination
        );
    }

    @Override
    @Transactional
    public void add(DriverCreateRequest req) {
        Driver driver = DriverMapper.INSTANCE.fromCreateRequestToEntity(req);
        driverRepo.save(driver);
    }

    @Override
    @Transactional
    public DriverDTO get(Long id) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin tài xế", HttpStatus.NOT_FOUND));
        return DriverMapper.INSTANCE.fromEntityToDTO(driver);
    }

    @Override
    @Transactional
    public void update(Long id, DriverUpdateRequest req) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin tài xế", HttpStatus.NOT_FOUND));
        ObjectUtils.copyProperties(req, driver);
        driverRepo.save(driver);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        driverRepo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin tài xế", HttpStatus.NOT_FOUND));
        driverRepo.softDelete(id);
    }

    @Override
    @Transactional
    public DriverBusDTO getDriverForSchedule(Long scheduleId) {
        Long driverId = scheduleRepo.getDriverIdByScheduleId(scheduleId);
        log.info("Find driverId {} for scheduleId {}", driverId, scheduleId);
        Long busId = scheduleRepo.getBusIdByScheduleId(scheduleId);
        Bus bus = busRepository.findByBusId(busId)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin xe", HttpStatus.NOT_FOUND));
        return new DriverBusDTO(
                this.get(driverId),
                BusMapper.INSTANCE.toBusDTO(bus)
        );
    }

    @Override
    public List<DriverDTO> getAvailableDrivers(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        LocalDateTime adjustedArrivalTime = arrivalTime.plus( Duration.ofDays(1));
        return driverRepo.findAvailableDrivers(departureTime, adjustedArrivalTime)
                .stream()
                .map(DriverMapper.INSTANCE::fromEntityToDTO)
                .collect(Collectors.toList());
    }
}
