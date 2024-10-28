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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
//        List<Bus> buses = new ArrayList<>();
//
//        String licensePrefix = "51B-";
//
//        for (int i = 1; i <= 15; i++) {
//            String licensePlate = licensePrefix + String.format("%05d", 5000 + i);
//
//            if (i % 2 == 0) {
//                Bus sleeperBus = Bus.builder()
//                        .licensePlate(licensePlate)
//                        .busType("Limousine 22 Phòng")
//                        .busImage("https://res.cloudinary.com/dj1h07rea/image/upload/v1729513741/z3887861317674_dd3bc0afcea0760dc9d713aa3a2b0a3f_ibsgxb.jpg")
//                        .totalSeats(36)
//                        .floors(2)
//                        .registrationExpiry(LocalDate.now().plusYears(2))
//                        .expirationDate(LocalDate.now().plusYears(5))
//                        .build();
//                buses.add(sleeperBus);
//            } else {
//                Bus seatedBus = Bus.builder()
//                        .licensePlate(licensePlate)
//                        .busType("Giường nằm 34 chỗ")
//                        .busImage("https://res.cloudinary.com/dj1h07rea/image/upload/v1730118728/vi%CC%A3-tri%CC%81-so%CC%82%CC%81-ghe%CC%82%CC%81-xe-giu%CC%9Bo%CC%9B%CC%80ng-na%CC%86%CC%80m-1024x563_enuelc.jpg")
//                        .totalSeats(40)
//                        .floors(1)
//                        .registrationExpiry(LocalDate.now().plusYears(3))
//                        .expirationDate(LocalDate.now().plusYears(5))
//                        .build();
//                buses.add(seatedBus);
//            }
//        }
//
//        busRepository.saveAll(buses);

//        List<Route> routes = new ArrayList<>();
//
//        // Existing routes
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Vũng Tàu")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Vũng Tàu")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Vũng Tàu - Sài Gòn")
//                .departureLocation("Vũng Tàu")
//                .arrivalLocation("Sài Gòn")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Cần Thơ")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Cần Thơ")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Cần Thơ - Sài Gòn")
//                .departureLocation("Cần Thơ")
//                .arrivalLocation("Sài Gòn")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Đà Lạt")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Đà Lạt")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Đà Lạt - Sài Gòn")
//                .departureLocation("Đà Lạt")
//                .arrivalLocation("Sài Gòn")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Nha Trang")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Nha Trang")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Nha Trang - Sài Gòn")
//                .departureLocation("Nha Trang")
//                .arrivalLocation("Sài Gòn")
//                .build());
//
//        // New routes
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Phan Thiết")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Phan Thiết")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Phan Thiết - Sài Gòn")
//                .departureLocation("Phan Thiết")
//                .arrivalLocation("Sài Gòn")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Bến Tre")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Bến Tre")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Bến Tre - Sài Gòn")
//                .departureLocation("Bến Tre")
//                .arrivalLocation("Sài Gòn")
//                .build());
//
//        routeRepository.saveAll(routes);

//        LocalDateTime[] days = {
//                LocalDateTime.of(2024, 11, 3, 13, 20),
//                LocalDateTime.of(2024, 11, 4, 7, 30),
//                LocalDateTime.of(2024, 11, 5, 9, 45)
//        };
//
//        for (LocalDateTime startDate : days) {
//            for (int i = 0; i < 3; i++) {  // 3 trips per day
//                // Tính toán chỉ số xe buýt cho chuyến đi này
//                int busId = 10 + (i % 6); // Sử dụng ID từ 10 đến 15
//
//                // Tìm xe buýt theo ID
//                Bus bus = busRepository.findById((long) busId).orElseThrow(() -> new RuntimeException("Bus not found"));
//
//                LocalDateTime departureTime = startDate.plusHours(i * 4);  // Mỗi chuyến bắt đầu 4 giờ sau chuyến trước
//                LocalDateTime arrivalTime = departureTime.plusHours(7).plusMinutes(30);  // Thời gian đến khoảng 7 giờ 30 phút sau khi khởi hành
//
//                Schedule schedule = Schedule.builder()
//                        .bus(bus)
//                        .route(routeRepository.findByRouteName("Sài Gòn - Nha Trang"))
//                        .departureTime(departureTime)
//                        .arrivalTime(arrivalTime)
//                        .price(300000.0)
//                        .build();
//
//                // Pickup stops
//                Set<RouteStop> stops = new HashSet<>();
//                stops.add(RouteStop.builder().schedule(schedule).location("Vp Nguyễn Cư Trinh, Q1").stopOrder(1).arrivalTime(departureTime).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Nhà chờ Phương Trang (Đường Mai Chí Thọ)").stopOrder(2).arrivalTime(departureTime.plusMinutes(15)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã 4 Tây Hoà (RMK)").stopOrder(3).arrivalTime(departureTime.plusMinutes(20)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Bình Thái").stopOrder(4).arrivalTime(departureTime.plusMinutes(25)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Thủ Đức").stopOrder(5).arrivalTime(departureTime.plusMinutes(30)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("KDL Suối Tiên").stopOrder(6).arrivalTime(departureTime.plusMinutes(35)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Bến Xe Miền Đông Mới (Quầy 120)").stopOrder(7).arrivalTime(departureTime.plusMinutes(40)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Tân Vạn").stopOrder(8).arrivalTime(departureTime.plusMinutes(45)).stopType(StopType.PICKUP).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Vũng Tàu").stopOrder(9).arrivalTime(departureTime.plusMinutes(46)).stopType(StopType.PICKUP).build());
//
//                // Drop-off stops
//                stops.add(RouteStop.builder().schedule(schedule).location("UBND phường Cam Nghĩa").stopOrder(10).arrivalTime(arrivalTime.minusMinutes(45)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Mỹ Ca").stopOrder(11).arrivalTime(arrivalTime.minusMinutes(40)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Agribank CN Cam Lâm").stopOrder(12).arrivalTime(arrivalTime.minusMinutes(30)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Bưu điện Cam Lâm").stopOrder(13).arrivalTime(arrivalTime.minusMinutes(29)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Cam Hải").stopOrder(14).arrivalTime(arrivalTime.minusMinutes(25)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã ba Lập Định").stopOrder(15).arrivalTime(arrivalTime.minusMinutes(15)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Đà Lạt").stopOrder(16).arrivalTime(arrivalTime.minusMinutes(14)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Bến xe Diên Khánh").stopOrder(17).arrivalTime(arrivalTime.minusMinutes(13)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Trạm y tế Diên Khánh").stopOrder(18).arrivalTime(arrivalTime.minusMinutes(12)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Thành").stopOrder(19).arrivalTime(arrivalTime.minusMinutes(10)).stopType(StopType.DROPOFF).build());
//                stops.add(RouteStop.builder().schedule(schedule).location("Vp Thích Quảng Đức Nha Trang").stopOrder(20).arrivalTime(arrivalTime).stopType(StopType.DROPOFF).build());
//
//                schedule.setStops(stops);
//
//                // Save each schedule
//                scheduleRepository.save(schedule);
//            }
//        }
    }
}
