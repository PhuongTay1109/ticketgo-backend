package com.ticketgo;

import com.ticketgo.model.Bus;
import com.ticketgo.model.Seat;
import com.ticketgo.repository.*;
import com.ticketgo.service.EmailService;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class TicketgoBackEndApplication implements CommandLineRunner {

    private final BusRepository busRepository;
    private final RouteStopRepository routeStopRepository;
    private final ScheduleRepository scheduleRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScheduleService scheduleService;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(TicketgoBackEndApplication.class, args);
    }

    Set<Seat> initialize22Seats(Bus bus) {
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

    Set<Seat> initialize34Seats(Bus bus) {
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

    @Override
    public void run(String... args) throws Exception {
//        emailService.sendBookingInfo(1);
//        BusCompany admin = BusCompany.builder()
//                .email("admin@gmail.com")
//                .password(passwordEncoder.encode("your_secure_password")) // Mã hóa mật khẩu
//                .imageUrl("default-image-url") // Thêm URL ảnh mặc định nếu cần
//                .role(Role.BUS_COMPANY) // Thiết lập vai trò ADMIN hoặc role phù hợp
//                .provider(Provider.LOCAL) // Hoặc thiết lập provider nếu bạn có sử dụng OAuth
//                .isEnabled(true)
//                .isLocked(false)
//                .busCompanyName("TicketGo")
//                .contactEmail("contact@ticketgo.com")
//                .contactPhone("0979552239")
//                .address("1, Võ Văn Ngân, P. Linh Chiểu, Q. Thủ Đức, Tp. Hồ Chí Minh")
//                .description("Nhà xe TicketGo chuyên cung cấp dịch vụ vận chuyển chất lượng.")
//                .build();
//
//        userRepository.save(admin);
//        List<Bus> buses = new ArrayList<>();
//
//        String licensePrefix = "51B-";
//
//        for (int i = 1; i <= 15; i++) {
//            String licensePlate = licensePrefix + String.format("%05d", 5000 + i);
//
//            Bus bus;
//            if (i % 2 == 0) {
//                bus = Bus.builder()
//                        .licensePlate(licensePlate)
//                        .busType("Limousine 22 Phòng")
//                        .busImage("https://res.cloudinary.com/dj1h07rea/image/upload/v1729513741/z3887861317674_dd3bc0afcea0760dc9d713aa3a2b0a3f_ibsgxb.jpg")
//                        .totalSeats(22)
//                        .floors(2)
//                        .registrationExpiry(LocalDate.now().plusYears(2))
//                        .expirationDate(LocalDate.now().plusYears(5))
//                        .build();
//                bus.setSeats(initialize22Seats(bus)); // Pass bus to createSeats
//            } else {
//                bus = Bus.builder()
//                        .licensePlate(licensePlate)
//                        .busType("Giường nằm 34 chỗ")
//                        .busImage("https://res.cloudinary.com/dj1h07rea/image/upload/v1730118728/vi%CC%A3-tri%CC%81-so%CC%82%CC%81-ghe%CC%82%CC%81-xe-giu%CC%9Bo%CC%9B%CC%80ng-na%CC%86%CC%80m-1024x563_enuelc.jpg")
//                        .totalSeats(34)
//                        .floors(2)
//                        .registrationExpiry(LocalDate.now().plusYears(3))
//                        .expirationDate(LocalDate.now().plusYears(5))
//                        .build();
//                bus.setSeats(initialize34Seats(bus)); // Pass bus to createSeats
//            }
//            buses.add(bus);
//        }
//
//        busRepository.saveAll(buses);
//
//        List<Route> routes = new ArrayList<>();
//
//        // Existing routes
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Vũng Tàu")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Vũng Tàu")
//                        .departureAddress("")
//                        .arrivalAddress("")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Vũng Tàu - Sài Gòn")
//                .departureLocation("Vũng Tàu")
//                .arrivalLocation("Sài Gòn")
//                .departureAddress("")
//                .arrivalAddress("")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Đà Lạt")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Đà Lạt")
//                .departureAddress("")
//                .arrivalAddress("")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Đà Lạt - Sài Gòn")
//                .departureLocation("Đà Lạt")
//                .arrivalLocation("Sài Gòn")
//                .departureAddress("")
//                .arrivalAddress("")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Nha Trang")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Nha Trang")
//                .departureAddress("Vp Nguyễn Cư Trinh, Q1")
//                        .arrivalAddress("Vp Thích Quảng Đức Nha Trang")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Nha Trang - Sài Gòn")
//                .departureLocation("Nha Trang")
//                .arrivalLocation("Sài Gòn")
//                .departureAddress("")
//                .arrivalAddress("")
//                .build());
//
//        // New routes
//        routes.add(Route.builder()
//                .routeName("Sài Gòn - Phan Thiết")
//                .departureLocation("Sài Gòn")
//                .arrivalLocation("Phan Thiết")
//                .departureAddress("")
//                .arrivalAddress("")
//                .build());
//
//        routes.add(Route.builder()
//                .routeName("Phan Thiết - Sài Gòn")
//                .departureLocation("Phan Thiết")
//                .arrivalLocation("Sài Gòn")
//                .departureAddress("")
//                .arrivalAddress("")
//                .build());
//
//        routeRepository.saveAll(routes);
//
//        LocalDateTime[] days = {
//                LocalDateTime.of(2024, 11, 20, 13, 20),
//                LocalDateTime.of(2024, 11, 21, 7, 30),
//                LocalDateTime.of(2024, 11, 22, 9, 45)
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
//                        .price(0.0)
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
//    }
//        for (long i = 1; i <= 9; i++) {
//            Schedule schedule = scheduleService.findById(i);
//            Set<Seat> seats = schedule.getBus().getSeats();
//            double price = 500000.0;
//            if (i==2) {
//                price=400000.0;
//            } else if (i==3) {
//                price=300000.0;
//            } else if (i==4) {
//                price=450000.0;
//            } else if (i==5) {
//                price=600000.0;
//            }
//            for(Seat seat : seats) {
//                Ticket ticket = Ticket.builder()
//                        .seat(seat)
//                        .schedule(schedule)
//                        .status(TicketStatus.AVAILABLE)
//                        .price(price)
//                        .build();
//                ticketRepository.save(ticket);
//            }
//            schedule.setPrice(price);
//            scheduleRepository.save(schedule);
//        }
    }
}
