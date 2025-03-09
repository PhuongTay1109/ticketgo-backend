package com.ticketgo.service.impl;

import com.ticketgo.dto.BusDTO;
import com.ticketgo.entity.Bus;
import com.ticketgo.entity.Seat;
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

import java.util.HashSet;
import java.util.Set;
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
        savedBus.setSeats(dto.getTotalSeats() == 22
                ? initialize22Seats(savedBus)
                : initialize34Seats(savedBus));
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

    private Set<Seat> initialize22Seats(Bus bus) {
        String[][][] seatStructure = {
                { // Floor 1: Ghế A và B
                        {"1A", "1B"}, {"2A", "2B"}, {"3A", "3B"},
                        {"4A", "4B"}, {"5A", "5B"}, {"6A", ""}
                },
                { // Floor 2: Ghế C và D
                        {"1C", "1D"}, {"2C", "2D"}, {"3C", "3D"},
                        {"4C", "4D"}, {"5C", "5D"}, {"6C", ""}
                }
        };

        Set<Seat> seats = new HashSet<>();
        for (int floor = 0; floor < seatStructure.length; floor++) {
            for (String[] row : seatStructure[floor]) {
                for (String seat : row) {
                    if (!seat.isEmpty()) {
                        Seat seatEntity = new Seat();
                        seatEntity.setBus(bus);
                        seatEntity.setFloor(floor + 1);
                        seatEntity.setSeatNumber(seat);
                        seatEntity.setRow(Integer.parseInt(seat.substring(0, 1))); // Số hàng
                        seatEntity.setCol(seat.substring(1)); // Cột (ví dụ: A, B, C, D)
                        seats.add(seatEntity);
                    }
                }
            }
        }
        return seats;
    }

    private Set<Seat> initialize34Seats(Bus bus) {
        String[][][] seatStructure = {
                { // Floor 1: Ghế A, B, C
                        {"1A", "1B", "1C"}, {"2A", "2B", "2C"}, {"3A", "3B", "3C"},
                        {"4A", "4B", "4C"}, {"5A", "5B", "5C"}, {"6A", "6B"}
                },
                { // Floor 2: Ghế D, E, F
                        {"1D", "1E", "1F"}, {"2D", "2E", "2F"}, {"3D", "3E", "3F"},
                        {"4D", "4E", "4F"}, {"5D", "5E", "5F"}, {"6D", "6E"}
                }
        };

        Set<Seat> seats = new HashSet<>();
        for (int floor = 0; floor < seatStructure.length; floor++) {
            for (String[] row : seatStructure[floor]) {
                for (String seat : row) {
                    if (!seat.isEmpty()) {
                        Seat seatEntity = new Seat();
                        seatEntity.setBus(bus);
                        seatEntity.setFloor(floor + 1);
                        seatEntity.setSeatNumber(seat);
                        seatEntity.setRow(Integer.parseInt(seat.substring(0, 1))); // Số hàng
                        seatEntity.setCol(seat.substring(1)); // Cột (ví dụ: A, B, C, D, E, F)
                        seats.add(seatEntity);
                    }
                }
            }
        }
        return seats;
    }
}
