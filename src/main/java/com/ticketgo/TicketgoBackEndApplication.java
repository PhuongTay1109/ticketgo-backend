package com.ticketgo;

import com.ticketgo.model.*;
import com.ticketgo.repository.BusRepository;
import com.ticketgo.repository.RouteRepository;
import com.ticketgo.repository.RouteStopRepository;
import com.ticketgo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@RequiredArgsConstructor
public class TicketgoBackEndApplication implements CommandLineRunner {

    private final BusRepository busRepository;
    private final RouteStopRepository routeStopRepository;
    private final ScheduleRepository scheduleRepository;
    private final RouteRepository routeRepository;

    public static void main(String[] args) {
        SpringApplication.run(TicketgoBackEndApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Bus newBus = Bus.builder()
                .licensePlate("XYZ-123")
                .busType("Xe giường nằm 40 ghế")
                .totalSeats(40)
                .floors(2)
                .registrationExpiry(LocalDate.of(2025, 10, 1))
                .expirationDate(LocalDate.of(2026, 10, 1))
                .build();

        busRepository.save(newBus);

        Bus newBus1 = Bus.builder()
                .licensePlate("ABC-123")
                .busType("Xe ghế ngồi 20 ghế")
                .totalSeats(20)
                .floors(1)
                .registrationExpiry(LocalDate.of(2025, 10, 1))
                .expirationDate(LocalDate.of(2026, 10, 1))
                .build();

        busRepository.save(newBus1);

        Bus newBus2 = Bus.builder()
                .licensePlate("DEF-456")  // Biển số mới
                .busType("Xe giường nằm 40 ghế")
                .totalSeats(40)
                .floors(2)
                .registrationExpiry(LocalDate.of(2025, 10, 1))
                .expirationDate(LocalDate.of(2026, 10, 1))
                .build();

        busRepository.save(newBus2);

        Bus newBus3 = Bus.builder()
                .licensePlate("GHI-789")  // Biển số mới
                .busType("Xe ghế ngồi 20 ghế")
                .totalSeats(20)
                .floors(1)
                .registrationExpiry(LocalDate.of(2025, 10, 1))
                .expirationDate(LocalDate.of(2026, 10, 1))
                .build();

        busRepository.save(newBus3);

        // Tạo các tuyến xe
        Route route1 = Route.builder()
                .routeName("Hà Nội - Hồ Chí Minh")
                .departureLocation("Hà Nội")
                .arrivalLocation("TP. Hồ Chí Minh")
                .build();

        Route route2 = Route.builder()
                .routeName("Đà Nẵng - Nha Trang")
                .departureLocation("Đà Nẵng")
                .arrivalLocation("Nha Trang")
                .build();

        routeRepository.save(route1);
        routeRepository.save(route2);

// Tạo lịch trình cho từng xe
        Schedule schedule1 = Schedule.builder()
                .bus(newBus) // Xe giường nằm 40 ghế
                .route(route1)
                .departureTime(LocalDateTime.of(2024, 10, 25, 8, 0))
                .arrivalTime(LocalDateTime.of(2024, 10, 25, 20, 0))
                .price(500000.0) // Giá vé
                .build();

        Schedule schedule2 = Schedule.builder()
                .bus(newBus1) // Xe ghế ngồi 20 ghế
                .route(route1)
                .departureTime(LocalDateTime.of(2024, 10, 25, 9, 0))
                .arrivalTime(LocalDateTime.of(2024, 10, 25, 21, 0))
                .price(400000.0) // Giá vé
                .build();

        Schedule schedule3 = Schedule.builder()
                .bus(newBus2) // Xe giường nằm 40 ghế
                .route(route2)
                .departureTime(LocalDateTime.of(2024, 10, 26, 9, 0))
                .arrivalTime(LocalDateTime.of(2024, 10, 26, 15, 0))
                .price(600000.0) // Giá vé
                .build();

        Schedule schedule4 = Schedule.builder()
                .bus(newBus3) // Xe ghế ngồi 20 ghế
                .route(route2)
                .departureTime(LocalDateTime.of(2024, 10, 26, 10, 0))
                .arrivalTime(LocalDateTime.of(2024, 10, 26, 16, 0))
                .price(500000.0) // Giá vé
                .build();

// Lưu lịch trình vào cơ sở dữ liệu
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
        scheduleRepository.save(schedule3);
        scheduleRepository.save(schedule4);

// Tạo trạm dừng cho lịch trình 1
        RouteStop stop1 = RouteStop.builder()
                .schedule(schedule1)
                .location("Ninh Bình")
                .stopOrder(1)
                .arrivalTime(LocalDateTime.of(2024, 10, 25, 10, 0))
                .stopType(StopType.PICKUP) // Hoặc STOP, DROP_OFF
                .build();

        RouteStop stop2 = RouteStop.builder()
                .schedule(schedule1)
                .location("Thanh Hóa")
                .stopOrder(2)
                .arrivalTime(LocalDateTime.of(2024, 10, 25, 12, 0))
                .stopType(StopType.PICKUP) // Hoặc STOP, DROP_OFF
                .build();

        routeStopRepository.save(stop1);
        routeStopRepository.save(stop2);

// Tạo trạm dừng cho lịch trình 2
        RouteStop stop3 = RouteStop.builder()
                .schedule(schedule2)
                .location("Nam Định")
                .stopOrder(1)
                .arrivalTime(LocalDateTime.of(2024, 10, 25, 11, 0))
                .stopType(StopType.PICKUP) // Hoặc STOP, DROP_OFF
                .build();

        routeStopRepository.save(stop3);

// Tạo trạm dừng cho lịch trình 3
        RouteStop stop4 = RouteStop.builder()
                .schedule(schedule3)
                .location("Phan Thiết")
                .stopOrder(1)
                .arrivalTime(LocalDateTime.of(2024, 10, 26, 11, 0))
                .stopType(StopType.PICKUP) // Hoặc STOP, DROP_OFF
                .build();

        routeStopRepository.save(stop4);

// Tạo trạm dừng cho lịch trình 4
        RouteStop stop5 = RouteStop.builder()
                .schedule(schedule4)
                .location("Cam Ranh")
                .stopOrder(1)
                .arrivalTime(LocalDateTime.of(2024, 10, 26, 12, 0))
                .stopType(StopType.PICKUP) // Hoặc STOP, DROP_OFF
                .build();

        routeStopRepository.save(stop5);

    }
}
