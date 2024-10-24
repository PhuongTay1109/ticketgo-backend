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
import java.util.HashSet;
import java.util.Set;

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
//        Bus bus1 = Bus.builder()
//                .licensePlate("29B-12345")
//                .busType("Xe giường nằm 40 ghế")
//                .busImage("https://res.cloudinary.com/dj1h07rea/image/upload/v1729513741/z3887861317674_dd3bc0afcea0760dc9d713aa3a2b0a3f_ibsgxb.jpg")
//                .totalSeats(40)
//                .floors(1)
//                .registrationExpiry(LocalDate.now().plusYears(2))
//                .expirationDate(LocalDate.now().plusYears(2))
//                .build();
//
//        Bus bus2 = Bus.builder()
//                .licensePlate("29B-67890")
//                .busType("Xe ghế ngồi 45 ghế")
//                .busImage("https://res.cloudinary.com/dj1h07rea/image/upload/v1729513741/z3887861317675_dd3bc0afcea0760dc9d713aa3a2b0a3f_ibsgxb.jpg")
//                .totalSeats(45)
//                .floors(1)
//                .registrationExpiry(LocalDate.now().plusYears(3))
//                .expirationDate(LocalDate.now().plusYears(3))
//                .build();
//
//        // Lưu các bus vào database
//        busRepository.save(bus1);
//        busRepository.save(bus2);
//
//        // Tạo đối tượng Route
//        Route route1 = Route.builder()
//                .routeName("Đà Nẵng - Nha Trang")
//                .departureLocation("Đà Nẵng")
//                .arrivalLocation("Nha Trang")
//                .build();
//
//        Route route2 = Route.builder()
//                .routeName("Hà Nội - Hạ Long")
//                .departureLocation("Hà Nội")
//                .arrivalLocation("Hạ Long")
//                .build();
//
//        // Lưu các route vào database
//        routeRepository.save(route1);
//        routeRepository.save(route2);
//
//        // Tạo đối tượng Schedule
//        Schedule schedule1 = Schedule.builder()
//                .bus(bus1)
//                .route(route1)
//                .departureTime(LocalDateTime.of(2024, 10, 26, 9, 0))
//                .arrivalTime(LocalDateTime.of(2024, 10, 26, 15, 0))
//                .price(200.0)
//                .build();
//
//        Schedule schedule2 = Schedule.builder()
//                .bus(bus2)
//                .route(route2)
//                .departureTime(LocalDateTime.of(2024, 10, 27, 8, 30))
//                .arrivalTime(LocalDateTime.of(2024, 10, 27, 10, 30))
//                .price(150.0)
//                .build();
//
//        // Lưu các schedule vào database
//        scheduleRepository.save(schedule1);
//        scheduleRepository.save(schedule2);
//
//        // Tạo đối tượng RouteStop cho schedule1
//        Set<RouteStop> stops1 = new HashSet<>();
//        stops1.add(RouteStop.builder()
//                .schedule(schedule1)
//                .location("Quảng Ngãi")
//                .stopOrder(1)
//                .arrivalTime(LocalDateTime.of(2024, 10, 26, 11, 0))
//                .stopType(StopType.PICKUP)
//                .build());
//
//        stops1.add(RouteStop.builder()
//                .schedule(schedule1)
//                .location("Nha Trang")
//                .stopOrder(2)
//                .arrivalTime(LocalDateTime.of(2024, 10, 26, 15, 0))
//                .stopType(StopType.DROPOFF)
//                .build());
//
//        schedule1.setStops(stops1);
//        // Lưu các RouteStop cho schedule1 vào database
//        routeStopRepository.saveAll(stops1);

//        // Tạo đối tượng RouteStop cho schedule2
//        Set<RouteStop> stops2 = new HashSet<>();
//        stops2.add(RouteStop.builder()
//                .schedule(schedule2)
//                .location("Hải Dương")
//                .stopOrder(1)
//                .arrivalTime(LocalDateTime.of(2024, 10, 27, 9, 0))
//                .stopType(StopType.PICKUP)
//                .build());
//
//        stops2.add(RouteStop.builder()
//                .schedule(schedule2)
//                .location("Hạ Long")
//                .stopOrder(2)
//                .arrivalTime(LocalDateTime.of(2024, 10, 27, 10, 30))
//                .stopType(StopType.DROPOFF)
//                .build());
//
//        schedule2.setStops(stops2);
//        // Lưu các RouteStop cho schedule2 vào database
//        routeStopRepository.saveAll(stops2);
    }
}
