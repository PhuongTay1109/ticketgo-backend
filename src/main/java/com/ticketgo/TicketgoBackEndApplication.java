package com.ticketgo;

import com.ticketgo.model.*;
import com.ticketgo.repository.*;
import com.ticketgo.service.EmailService;
import com.ticketgo.service.ScheduleService;
import com.ticketgo.service.TokenService;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
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
    private final TokenService tokenService;
    private final UserService userService;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
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
//        User user = userService.findByEmail("phuonggteyy@gmail.com");
//        Token token = tokenService.createToken(user, TokenType.ACTIVATION);
//        emailService.sendActivationEmail(user.getEmail(), token.getValue())
//                .thenAccept(success -> {
//                    if (success) {
//                        log.info("Email sent successfully!");
//                    } else {
//                        log.error("Email sending failed.");
//                    }
//                })
//                .exceptionally(ex -> {
//                    log.error("Failed to send email: {}", ex.getMessage());
//                    return null;
//                });
//        BusCompany admin = (BusCompany) userRepository.findByEmail("admin@gmail.com").orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        admin.setRole(Role.ROLE_BUS_COMPANY);
//        userRepository.save(admin);
//        admin.setDescription("Nhà xe TicketGo là đơn vị chuyên cung cấp dịch vụ vận chuyển chất lượng cao, hướng đến sự hài lòng và an tâm của khách hàng trong từng hành trình. Với sứ mệnh trở thành người bạn đồng hành đáng tin cậy, chúng tôi không ngừng cải thiện và nâng cấp các dịch vụ để mang đến những trải nghiệm tốt nhất.\n" +
//                "\n" +
//                "TicketGo tự hào sở hữu đội ngũ lái xe giàu kinh nghiệm, thân thiện và được đào tạo chuyên nghiệp, cùng với hệ thống xe hiện đại, sạch sẽ và an toàn. Chúng tôi cung cấp đa dạng các tuyến đường, linh hoạt đáp ứng nhu cầu di chuyển của khách hàng, từ vận chuyển cá nhân, gia đình đến các đoàn thể, tổ chức.\n" +
//                "\n" +
//                "Bên cạnh đó, TicketGo áp dụng công nghệ tiên tiến vào hệ thống đặt vé trực tuyến, giúp khách hàng dễ dàng tìm kiếm tuyến đường, lựa chọn chỗ ngồi và thanh toán nhanh chóng, tiện lợi. Chúng tôi cam kết minh bạch trong giá cả, hỗ trợ khách hàng tận tình và xử lý mọi yêu cầu một cách nhanh chóng.\n" +
//                "\n" +
//                "Với tôn chỉ \"Chất lượng - An toàn - Uy tín,\" TicketGo luôn nỗ lực từng ngày để mang đến những chuyến đi thoải mái, đúng giờ và đáng nhớ. Chúng tôi trân trọng sự tin tưởng của khách hàng và sẽ tiếp tục phát triển để trở thành sự lựa chọn hàng đầu trong lĩnh vực vận chuyển.");
//        admin.setPassword(passwordEncoder.encode("Admin123"));
//        admin.setBannerUrl("https://static.vexere.com/production/banners/1209/vi_leaderboard_1440x480_1.jpg");
//        admin.setImageUrl("https://res.cloudinary.com/dj1h07rea/image/upload/v1732419422/03809988294a4197849715dd0850f3b8-free_mnt4dp.png");
//        userRepository.save(admin);
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
//        for (long i = 10; i <= 12; i++) {
//            Schedule schedule = scheduleService.findById(i);
//            Set<Seat> seats = schedule.getBus().getSeats();
//            double price = 400000.0;
//            if (i==11) {
//                price=450000.0;
//            } else if (i==12) {
//                price=500000.0;
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
//        LocalDateTime startDate = LocalDateTime.of(2024, 12, 24, 7, 0); // Bắt đầu từ ngày 24/12
//
//        for (int i = 0; i < 3; i++) {  // 3 chuyến trong ngày
//            // Tính toán chỉ số xe buýt cho chuyến đi này
//            int busId = 10 + (i % 6); // Sử dụng ID từ 10 đến 15
//
//            // Tìm xe buýt theo ID
//            Bus bus = busRepository.findById((long) busId)
//                    .orElseThrow(() -> new RuntimeException("Bus not found"));
//
//            LocalDateTime departureTime = startDate.plusHours(i * 4);  // Mỗi chuyến cách nhau 4 giờ
//            LocalDateTime arrivalTime = departureTime.plusHours(7).plusMinutes(30);  // Thời gian đến sau 7 giờ 30 phút
//
//            Schedule schedule = Schedule.builder()
//                    .bus(bus)
//                    .route(routeRepository.findByRouteName("Sài Gòn - Nha Trang"))
//                    .departureTime(departureTime)
//                    .arrivalTime(arrivalTime)
//                    .price(0.0)
//                    .isVisible(true)
//                    .build();
//
//            // Pickup stops
//            Set<RouteStop> stops = new HashSet<>();
//            stops.add(RouteStop.builder().schedule(schedule).location("Vp Nguyễn Cư Trinh, Q1").stopOrder(1).arrivalTime(departureTime).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Nhà chờ Phương Trang (Đường Mai Chí Thọ)").stopOrder(2).arrivalTime(departureTime.plusMinutes(15)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã 4 Tây Hoà (RMK)").stopOrder(3).arrivalTime(departureTime.plusMinutes(20)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Bình Thái").stopOrder(4).arrivalTime(departureTime.plusMinutes(25)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Thủ Đức").stopOrder(5).arrivalTime(departureTime.plusMinutes(30)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("KDL Suối Tiên").stopOrder(6).arrivalTime(departureTime.plusMinutes(35)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Bến Xe Miền Đông Mới (Quầy 120)").stopOrder(7).arrivalTime(departureTime.plusMinutes(40)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Tân Vạn").stopOrder(8).arrivalTime(departureTime.plusMinutes(45)).stopType(StopType.PICKUP).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Vũng Tàu").stopOrder(9).arrivalTime(departureTime.plusMinutes(46)).stopType(StopType.PICKUP).build());
//
//            // Drop-off stops
//            stops.add(RouteStop.builder().schedule(schedule).location("UBND phường Cam Nghĩa").stopOrder(10).arrivalTime(arrivalTime.minusMinutes(45)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Mỹ Ca").stopOrder(11).arrivalTime(arrivalTime.minusMinutes(40)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Agribank CN Cam Lâm").stopOrder(12).arrivalTime(arrivalTime.minusMinutes(30)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Bưu điện Cam Lâm").stopOrder(13).arrivalTime(arrivalTime.minusMinutes(29)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Cam Hải").stopOrder(14).arrivalTime(arrivalTime.minusMinutes(25)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã ba Lập Định").stopOrder(15).arrivalTime(arrivalTime.minusMinutes(15)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Đà Lạt").stopOrder(16).arrivalTime(arrivalTime.minusMinutes(14)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Bến xe Diên Khánh").stopOrder(17).arrivalTime(arrivalTime.minusMinutes(13)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Trạm y tế Diên Khánh").stopOrder(18).arrivalTime(arrivalTime.minusMinutes(12)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Thành").stopOrder(19).arrivalTime(arrivalTime.minusMinutes(10)).stopType(StopType.DROPOFF).build());
//            stops.add(RouteStop.builder().schedule(schedule).location("Vp Thích Quảng Đức Nha Trang").stopOrder(20).arrivalTime(arrivalTime).stopType(StopType.DROPOFF).build());
//
//            schedule.setStops(stops);
//
//            // Save each schedule
//            scheduleRepository.save(schedule);
//        }
//        LocalDateTime startDate = LocalDateTime.of(2024, 12, 31, 23, 30); // Bắt đầu từ ngày 31/12, 23:30
//
//// Tìm xe buýt theo ID
//        int busId = 1; // Sử dụng ID cho tuyến Sài Gòn - Đà Lạt
//        Bus bus = busRepository.findById((long) busId)
//                .orElseThrow(() -> new RuntimeException("Bus not found"));
//
//// Thời gian khởi hành và đến nơi
//        LocalDateTime departureTime = startDate;
//        LocalDateTime arrivalTime = LocalDateTime.of(2025, 1, 1, 7, 30); // Đến Đà Lạt lúc 07:30 ngày 01/01
//
//        Schedule schedule = Schedule.builder()
//                .bus(bus)
//                .route(routeRepository.findByRouteName("Sài Gòn - Đà Lạt"))
//                .departureTime(departureTime)
//                .arrivalTime(arrivalTime)
//                .price(0.0)
//                .isVisible(true)
//                .build();
//
//// Pickup stops
//        Set<RouteStop> stops = new HashSet<>();
//        stops.add(RouteStop.builder().schedule(schedule).location("VP Tân Bình").stopOrder(1).arrivalTime(departureTime).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Lăng Cha Cả").stopOrder(2).arrivalTime(departureTime.plusMinutes(10)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Phú Nhuận").stopOrder(3).arrivalTime(departureTime.plusMinutes(15)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Chợ Bà Chiểu").stopOrder(4).arrivalTime(departureTime.plusMinutes(20)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Bạch Đằng - Đinh Bộ Lĩnh").stopOrder(5).arrivalTime(departureTime.plusMinutes(25)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Trạm Bus Metro Quận 2").stopOrder(6).arrivalTime(departureTime.plusMinutes(30)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã 4 Tây Hoà (RMK)").stopOrder(7).arrivalTime(departureTime.plusMinutes(35)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Bình Thái").stopOrder(8).arrivalTime(departureTime.plusMinutes(36)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Văn Phòng Thủ Đức").stopOrder(9).arrivalTime(departureTime.plusMinutes(50)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Bến xe Miền Đông Mới").stopOrder(10).arrivalTime(departureTime.plusMinutes(60)).stopType(StopType.PICKUP).build());
//
//// Drop-off stops
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Madagui").stopOrder(11).arrivalTime(arrivalTime.minusHours(4)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Coop Mart Bảo Lộc").stopOrder(12).arrivalTime(arrivalTime.minusHours(3)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã ba Liên Khương").stopOrder(13).arrivalTime(arrivalTime.minusMinutes(90)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Cao tốc Liên Khương").stopOrder(14).arrivalTime(arrivalTime.minusMinutes(89)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Thu phí Định An - Cao tốc Liên Khương").stopOrder(15).arrivalTime(arrivalTime.minusMinutes(60)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("VP Đà Lạt").stopOrder(16).arrivalTime(arrivalTime).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Nội thành Đà Lạt (bán kính 7km)").stopOrder(17).arrivalTime(arrivalTime).stopType(StopType.DROPOFF).build());
//
//        schedule.setStops(stops);
//
//// Lưu lịch trình
//        scheduleRepository.save(schedule);
//
//        for (long i = 13; i <= 14; i++) {
//            Schedule schedule = scheduleService.findById(i);
//            Set<Seat> seats = schedule.getBus().getSeats();
//            double price = 400000.0;
//            if (i==14)
//                price=350000.0;
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

//        LocalDateTime startDate = LocalDateTime.of(2024, 12, 31, 7, 0); // Chuyến 7:00 sáng ngày 31/12
//        int busId = 10; // ID xe buýt
//
//// Tìm xe buýt theo ID
//        Bus bus = busRepository.findById((long) busId)
//                .orElseThrow(() -> new RuntimeException("Bus not found"));
//
//        LocalDateTime departureTime = startDate;
//        LocalDateTime arrivalTime = departureTime.plusHours(7).plusMinutes(30); // Thời gian đến sau 7 giờ 30 phút
//
//        Schedule schedule = Schedule.builder()
//                .bus(bus)
//                .route(routeRepository.findByRouteName("Sài Gòn - Đà Lạt"))
//                .departureTime(departureTime)
//                .arrivalTime(arrivalTime)
//                .price(0.0)
//                .isVisible(true)
//                .build();
//
//// Pickup stops
//        Set<RouteStop> stops = new HashSet<>();
//        stops.add(RouteStop.builder().schedule(schedule).location("VP Tân Bình").stopOrder(1).arrivalTime(departureTime).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Lăng Cha Cả").stopOrder(2).arrivalTime(departureTime.plusMinutes(10)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã Tư Phú Nhuận").stopOrder(3).arrivalTime(departureTime.plusMinutes(15)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Chợ Bà Chiểu").stopOrder(4).arrivalTime(departureTime.plusMinutes(20)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Bạch Đằng - Đinh Bộ Lĩnh").stopOrder(5).arrivalTime(departureTime.plusMinutes(25)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Trạm Bus Metro Quận 2").stopOrder(6).arrivalTime(departureTime.plusMinutes(30)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã 4 Tây Hoà (RMK)").stopOrder(7).arrivalTime(departureTime.plusMinutes(35)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã tư Bình Thái").stopOrder(8).arrivalTime(departureTime.plusMinutes(36)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Văn Phòng Thủ Đức").stopOrder(9).arrivalTime(departureTime.plusMinutes(37)).stopType(StopType.PICKUP).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Bến xe Miền Đông Mới").stopOrder(10).arrivalTime(departureTime.plusMinutes(50)).stopType(StopType.PICKUP).build());
//
//// Drop-off stops
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã 3 Madagui").stopOrder(11).arrivalTime(arrivalTime.minusMinutes(180)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Coop Mart Bảo Lộc").stopOrder(12).arrivalTime(arrivalTime.minusMinutes(120)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Ngã ba Liên Khương").stopOrder(13).arrivalTime(arrivalTime.minusMinutes(90)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Cao tốc Liên Khương").stopOrder(14).arrivalTime(arrivalTime.minusMinutes(89)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("Thu phí Định An - Cao tốc Liên Khương").stopOrder(15).arrivalTime(arrivalTime.minusMinutes(60)).stopType(StopType.DROPOFF).build());
//        stops.add(RouteStop.builder().schedule(schedule).location("VP Đà Lạt").stopOrder(16).arrivalTime(arrivalTime).stopType(StopType.DROPOFF).build());
//
//        schedule.setStops(stops);
//
//// Lưu lịch trình
//        scheduleRepository.save(schedule);

    }
}
