package com.ticketgo.service.impl;

import com.ticketgo.dto.BookingInfoDTO;
import com.ticketgo.entity.Bus;
import com.ticketgo.entity.Driver;
import com.ticketgo.entity.Schedule;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.EmailService;
import com.ticketgo.service.GmailService;
import com.ticketgo.service.ScheduleService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    private final BookingRepository bookingRepository;
    private BookingService bookingService;
    private ScheduleService scheduleService;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            @Lazy BookingService bookingService,
                            @Lazy ScheduleService scheduleService, BookingRepository bookingRepository) {
        this.emailSender = emailSender;
        this.bookingService = bookingService;
        this.scheduleService = scheduleService;
        this.bookingRepository = bookingRepository;
    }


    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.fromName}")
    private String fromName;

    @Value("${frontend.url}")
    private String feUrl;

    @Async
    @Override
    public CompletableFuture<Boolean> sendActivationEmail(String email, String token) {
        try {
//            MimeMessage message = createActivationEmail(email, token);
//            emailSender.send(message);
            GmailService.sendEmail(
                    email,
                    getActivationEmailSubject(),
                    getActivationEmailContent(token));
            return CompletableFuture.completedFuture(true);
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Failed to send activation email to {}: {}", email, e.getMessage());
            return CompletableFuture.completedFuture(false);
        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", email, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendBookingInfo(long bookingId, long scheduleId) {
        List<BookingInfoDTO> bookingInfoList = bookingService.getBookingInfoList(bookingId);
        log.info("Booking info list: {}", bookingInfoList.size());

        Schedule schedule = scheduleService.findById(scheduleId);
        Driver driver = schedule.getDriver();
        if (!bookingInfoList.isEmpty()) {
            String contactEmail = bookingInfoList.get(0).getContactEmail();
            String contactName = bookingInfoList.get(0).getContactName();
            String routeName = bookingInfoList.get(0).getRouteName();
            String departureDate = bookingInfoList.get(0).getDepartureDate();
            String lisensePlate = bookingInfoList.get(0).getLicensePlate();
            String pickupTime = bookingInfoList.get(0).getPickupTime();
            String pickupLocation = bookingInfoList.get(0).getPickupLocation();
            String dropoffLocation = bookingInfoList.get(0).getDropoffLocation();
            String bookingDate = bookingInfoList.get(0).getBookingDate();

            String seatInfo = "";

            for (int i = 0; i < bookingInfoList.size(); i++) {
                BookingInfoDTO info = bookingInfoList.get(i);
                seatInfo += info.getSeatNumber();
                if (i < bookingInfoList.size() - 1) {
                    seatInfo += " , ";
                }
            }

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<div style=\"font-family: 'Arial', sans-serif; max-width: 700px; margin: 0 auto; border: 1px solid #e1e1e1; border-radius: 8px; overflow: hidden; background-color: #f4f4f9;\">");

            // Header
            emailContent.append("<div style=\"background-color: #007BFF; padding: 20px; text-align: center; color: white; border-top-left-radius: 8px; border-top-right-radius: 8px;\">");
            emailContent.append("<h1 style=\"font-size: 24px; margin: 0;\">THÔNG TIN ĐẶT VÉ XE</h1>");
            emailContent.append("<p style=\"font-size: 14px; margin: 5px 0 0;\">Mã đặt vé: #" + bookingId + "</p>");
            emailContent.append("<p style=\"font-size: 12px; margin: 5px 0 0;\">Ngày đặt vé: " + bookingDate + "</p>");
            emailContent.append("</div>");

            // Customer Info
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-bottom: 1px solid #e1e1e1;\">");
            emailContent.append("<h2 style=\"font-size: 18px; color: #333; margin-top: 0;\">Xin chào " + contactName + ",</h2>");
            emailContent.append("<p style=\"color: #666; font-size: 14px;\">Cảm ơn bạn đã đặt vé với chúng tôi. Dưới đây là thông tin chi tiết chuyến đi của bạn:</p>");
            emailContent.append("</div>");

            // Trip Details
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff;\">");

            // Route Info
            emailContent.append("<div style=\"margin-bottom: 20px;\">");
            emailContent.append("<h3 style=\"font-size: 16px; color: #333; margin: 0;\">Chuyến đi: " + routeName + "</h3>");
            emailContent.append("<p style=\"color: #666; font-size: 14px;\"> Giờ khởi hành: " + departureDate + "</p>");
            emailContent.append("</div>");

            /// Pickup Info
            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<p style=\"font-size: 14px; color: #888; margin: 0; font-weight: bold;\">ĐIỂM ĐÓN:</p>");
            emailContent.append("<p style=\"font-size: 15px; color: #333; margin: 4px 0 0;\">" + pickupLocation + " • Thời gian đón dự kiến: " + pickupTime + "</p>");
            emailContent.append("</div>");

            // Dropoff Info
            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<p style=\"font-size: 14px; color: #888; margin: 0; font-weight: bold;\">ĐIỂM TRẢ:</p>");
            emailContent.append("<p style=\"font-size: 15px; color: #333; margin: 4px 0 0;\">" + dropoffLocation + "</p>");
            emailContent.append("</div>");


            emailContent.append("</div>");

            // Booking Details
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-top: 1px solid #e1e1e1;\">");
            emailContent.append("<h3 style=\"font-size: 16px; color: #333;\">CHI TIẾT ĐẶT VÉ</h3>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Số ghế đã đặt: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + seatInfo + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Biển số xe: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + lisensePlate + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Tên tài xế: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + driver.getName() + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Liên hệ tài xế: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + driver.getPhoneNumber() + "</span>");
            emailContent.append("</div>");

            emailContent.append("</div>");

            // Status
            emailContent.append("<div style=\"padding: 20px; background-color: #f2f7f4; text-align: center; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
            emailContent.append("<span style=\"background-color: #28a745; color: white; padding: 10px 20px; border-radius: 20px; font-weight: bold;\">✔ ĐÃ XÁC NHẬN</span>");
            emailContent.append("</div>");

            // Footer
            emailContent.append("<div style=\"background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
            emailContent.append("<p style=\"margin: 0 0 10px 0;\">Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này hoặc gọi đến hotline.</p>");
            emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\">Hotline: 0979552239</p>");
            emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\"><a href=\"https://ticketgo-black.vercel.app/\" style=\"color: #007BFF; text-decoration: none;\">TicketGo - Đặt vé xe dễ dàng và nhanh chóng </a></p>");
            emailContent.append("</div>");
            emailContent.append("</div>");

            try {
//                MimeMessage message = emailSender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//                helper.setFrom(fromEmail, fromName);
//                helper.setTo(contactEmail);
//                helper.setSubject("Thông tin đặt vé của bạn - Mã #" + bookingId);
//                helper.setText(emailContent.toString(), true);
//
//                emailSender.send(message);
//                log.info("Đã gửi email thông tin đặt vé tới: {}", contactEmail);
                GmailService.sendEmail(
                        contactEmail,
                        "Thông tin đặt vé của bạn - Mã #" + bookingId,
                        emailContent.toString()
                );
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
            } catch (Exception e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Async("taskExecutor")
    @Override
    public void sendResetPasswordEmail(String email, String token) {
        try {
//            MimeMessage message = createResetPasswordEmail(email, token);
//            emailSender.send(message);
//            log.info("Đã gửi email đặt lại mật khẩu tới: {}", email);
            GmailService.sendEmail(
                    email,
                    getResetPasswordEmailSubject(),
                    getResetPasswordEmailContent(token)
            );
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Failed to send reset password email to {}: {}", email, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send reset password email to {}: {}", email, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendBookingInfoReturn(Long bookingId, Long scheduleId, Long returnBookingId, Long returnScheduleId) {
        List<BookingInfoDTO> goBookingInfoList = bookingService.getBookingInfoList(bookingId);
        List<BookingInfoDTO> returnBookingInfoList = bookingService.getBookingInfoList(returnBookingId);

        if (goBookingInfoList.isEmpty() || returnBookingInfoList.isEmpty()) {
            log.warn("Booking info list for either go or return trip is empty.");
            return;
        }

        BookingInfoDTO goInfo = goBookingInfoList.get(0);
        BookingInfoDTO returnInfo = returnBookingInfoList.get(0);

        String contactEmail = goInfo.getContactEmail();
        String contactName = goInfo.getContactName();
        String bookingDate = goInfo.getBookingDate();

        // Chiều đi
        Schedule goSchedule = scheduleService.findById(scheduleId);
        Driver goDriver = goSchedule.getDriver();
        String goSeatInfo = goBookingInfoList.stream().map(BookingInfoDTO::getSeatNumber).collect(Collectors.joining(" , "));

        // Chiều về
        Schedule returnSchedule = scheduleService.findById(returnScheduleId);
        Driver returnDriver = returnSchedule.getDriver();
        String returnSeatInfo = returnBookingInfoList.stream().map(BookingInfoDTO::getSeatNumber).collect(Collectors.joining(" , "));

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<div style=\"font-family: 'Arial', sans-serif; max-width: 700px; margin: 0 auto; border: 1px solid #e1e1e1; border-radius: 8px; overflow: hidden; background-color: #f4f4f9;\">");

        // Header
        emailContent.append("<div style=\"background-color: #007BFF; padding: 20px; text-align: center; color: white; border-top-left-radius: 8px; border-top-right-radius: 8px;\">");
        emailContent.append("<h1 style=\"font-size: 24px; margin: 0;\">THÔNG TIN ĐẶT VÉ XE</h1>");
        emailContent.append("<p style=\"font-size: 14px; margin: 5px 0 0;\">Mã đặt vé: #" + bookingId + " / #" + returnBookingId + "</p>");
        emailContent.append("<p style=\"font-size: 12px; margin: 5px 0 0;\">Ngày đặt vé: " + bookingDate + "</p>");
        emailContent.append("</div>");

        // Intro
        emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-bottom: 1px solid #e1e1e1;\">");
        emailContent.append("<h2 style=\"font-size: 18px; color: #333; margin-top: 0;\">Xin chào " + contactName + ",</h2>");
        emailContent.append("<p style=\"color: #666; font-size: 14px;\">Cảm ơn bạn đã đặt vé với chúng tôi. Dưới đây là thông tin chi tiết chuyến đi của bạn:</p>");
        emailContent.append("</div>");

        // GO TRIP
        emailContent.append("<div style=\"padding: 20px; background-color: #ffffff;\">");
        emailContent.append("<h3 style=\"color: #007BFF;\">Chiều đi: " + goInfo.getRouteName() + "</h3>");
        emailContent.append("<p><b>Giờ khởi hành:</b> " + goInfo.getDepartureDate() + "</p>");
        emailContent.append("<p><b>Điểm đón:</b> " + goInfo.getPickupLocation() + "</p>");
        emailContent.append("<p><b>Thời gian đón dự kiến:</b> " + goInfo.getPickupTime() + "</p>");
        emailContent.append("<p><b>Điểm trả:</b> " + goInfo.getDropoffLocation() + "</p>");
        emailContent.append("<p><b>Số ghế:</b> " + goSeatInfo + "</p>");
        emailContent.append("<p><b>Biển số xe:</b> " + goInfo.getLicensePlate() + "</p>");
        emailContent.append("<p><b>Tên tài xế:</b> " + goDriver.getName() + "</p>");
        emailContent.append("<p><b>Liên hệ tài xế:</b> " + goDriver.getPhoneNumber() + "</p>");
        emailContent.append("</div>");

        // RETURN TRIP
        emailContent.append("<div style=\"padding: 20px; background-color: #ffffff;\">");
        emailContent.append("<h3 style=\"color: #007BFF;\">Chiều về: " + returnInfo.getRouteName() + "</h3>");
        emailContent.append("<p><b>Giờ khởi hành:</b> " + returnInfo.getDepartureDate() + "</p>");;
        emailContent.append("<p><b>Điểm đón:</b> " + returnInfo.getPickupLocation() + "</p>");
        emailContent.append("<p><b>Thời gian đón dự kiến:</b> " + goInfo.getPickupTime() + "</p>");
        emailContent.append("<p><b>Điểm trả:</b> " + returnInfo.getDropoffLocation() + "</p>");
        emailContent.append("<p><b>Số ghế:</b> " + returnSeatInfo + "</p>");
        emailContent.append("<p><b>Biển số xe:</b> " + returnInfo.getLicensePlate() + "</p>");
        emailContent.append("<p><b>Tên tài xế:</b> " + goDriver.getName() + "</p>");
        emailContent.append("<p><b>Liên hệ tài xế:</b> " + goDriver.getPhoneNumber() + "</p>");
        emailContent.append("</div>");

        // Confirmation
        emailContent.append("<div style=\"padding: 20px; background-color: #f2f7f4; text-align: center; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
        emailContent.append("<span style=\"background-color: #28a745; color: white; padding: 10px 20px; border-radius: 20px; font-weight: bold;\">✔ ĐÃ XÁC NHẬN</span>");
        emailContent.append("</div>");

        // Footer
        emailContent.append("<div style=\"background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
        emailContent.append("<p style=\"margin: 0 0 10px 0;\">Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này hoặc gọi đến hotline.</p>");
        emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\">Hotline: 0979552239</p>");
        emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\"><a href=\"https://ticketgo-black.vercel.app/\" style=\"color: #007BFF; text-decoration: none;\">TicketGo - Đặt vé xe dễ dàng và nhanh chóng</a></p>");
        emailContent.append("</div>");
        emailContent.append("</div>");

        try {
            GmailService.sendEmail(
                    contactEmail,
                    "Thông tin đặt vé của bạn - Mã #" + bookingId + " / #" + returnBookingId,
                    emailContent.toString()
            );
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public void sendUpdateDriver(Schedule schedule, long bookingId, Driver driver) {
        List<BookingInfoDTO> bookingInfoList = bookingService.getBookingInfoList(bookingId);
        log.info("Booking info list: {}", bookingInfoList.size());
        if (!bookingInfoList.isEmpty()) {
            String contactEmail = bookingInfoList.get(0).getContactEmail();
            String contactName = bookingInfoList.get(0).getContactName();
            String routeName = bookingInfoList.get(0).getRouteName();
            String departureDate = bookingInfoList.get(0).getDepartureDate();
            String lisensePlate = bookingInfoList.get(0).getLicensePlate();
            String pickupTime = bookingInfoList.get(0).getPickupTime();
            String pickupLocation = bookingInfoList.get(0).getPickupLocation();
            String dropoffLocation = bookingInfoList.get(0).getDropoffLocation();
            String bookingDate = bookingInfoList.get(0).getBookingDate();

            String seatInfo = "";

            for (int i = 0; i < bookingInfoList.size(); i++) {
                BookingInfoDTO info = bookingInfoList.get(i);
                seatInfo += info.getSeatNumber();
                if (i < bookingInfoList.size() - 1) {
                    seatInfo += " , ";
                }
            }

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<div style=\"font-family: 'Arial', sans-serif; max-width: 700px; margin: 0 auto; border: 1px solid #e1e1e1; border-radius: 8px; overflow: hidden; background-color: #f4f4f9;\">");

            // Header
            emailContent.append("<div style=\"background-color: #007BFF; padding: 20px; text-align: center; color: white; border-top-left-radius: 8px; border-top-right-radius: 8px;\">");
            emailContent.append("<h1 style=\"font-size: 24px; margin: 0;\">THÔNG TIN ĐẶT VÉ XE</h1>");
            emailContent.append("<p style=\"font-size: 14px; margin: 5px 0 0;\">Mã đặt vé: #" + bookingId + "</p>");
            emailContent.append("<p style=\"font-size: 12px; margin: 5px 0 0;\">Ngày đặt vé: " + bookingDate + "</p>");
            emailContent.append("</div>");

            // Customer Info
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-bottom: 1px solid #e1e1e1;\">");
            emailContent.append("<h2 style=\"font-size: 18px; color: #333; margin-top: 0;\">Xin chào " + contactName + ",</h2>");
            emailContent.append("<p style=\"color: #666; font-size: 14px;\">TicketGo xin gửi đến bạn thông tin cập nhật <span style='color: #d9534f; font-weight: bold;'>tài xế</span> cho chuyến đi:</p>");
            emailContent.append("</div>");

            // Trip Details
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff;\">");

            // Route Info
            emailContent.append("<div style=\"margin-bottom: 20px;\">");
            emailContent.append("<h3 style=\"font-size: 16px; color: #333; margin: 0;\">Chuyến đi: " + routeName + "</h3>");
            emailContent.append("<p style=\"color: #666; font-size: 14px;\"> Giờ khởi hành: " + departureDate + "</p>");
            emailContent.append("</div>");

            /// Pickup Info
            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<p style=\"font-size: 14px; color: #888; margin: 0; font-weight: bold;\">ĐIỂM ĐÓN:</p>");
            emailContent.append("<p style=\"font-size: 15px; color: #333; margin: 4px 0 0;\">" + pickupLocation + " • Thời gian đón dự kiến: " + pickupTime + "</p>");
            emailContent.append("</div>");

            // Dropoff Info
            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<p style=\"font-size: 14px; color: #888; margin: 0; font-weight: bold;\">ĐIỂM TRẢ:</p>");
            emailContent.append("<p style=\"font-size: 15px; color: #333; margin: 4px 0 0;\">" + dropoffLocation + "</p>");
            emailContent.append("</div>");


            emailContent.append("</div>");

            // Booking Details
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-top: 1px solid #e1e1e1;\">");
            emailContent.append("<h3 style=\"font-size: 16px; color: #333;\">CHI TIẾT ĐẶT VÉ</h3>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Số ghế đã đặt: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + seatInfo + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Biển số xe: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + lisensePlate + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px; padding: 10px; background-color: #e8f5e9; border-left: 4px solid #4caf50;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #2e7d32; font-weight: bold;\">🔄 Tên tài xế mới: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #2e7d32;\">" + driver.getName() + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px; padding: 10px; background-color: #e8f5e9; border-left: 4px solid #4caf50;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #2e7d32; font-weight: bold;\">📞 Liên hệ tài xế mới: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #2e7d32;\">" + driver.getPhoneNumber() + "</span>");
            emailContent.append("</div>");

            emailContent.append("</div>");

            // Status
            emailContent.append("<div style=\"padding: 20px; background-color: #f2f7f4; text-align: center; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
            emailContent.append("<span style=\"background-color: #28a745; color: white; padding: 10px 20px; border-radius: 20px; font-weight: bold;\">✔ ĐÃ XÁC NHẬN</span>");
            emailContent.append("</div>");

            // Footer
            emailContent.append("<div style=\"background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
            emailContent.append("<p style=\"margin: 0 0 10px 0;\">Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này hoặc gọi đến hotline.</p>");
            emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\">Hotline: 0979552239</p>");
            emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\"><a href=\"https://ticketgo-black.vercel.app/\" style=\"color: #007BFF; text-decoration: none;\">TicketGo - Đặt vé xe dễ dàng và nhanh chóng </a></p>");
            emailContent.append("</div>");
            emailContent.append("</div>");

            try {
//                MimeMessage message = emailSender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//                helper.setFrom(fromEmail, fromName);
//                helper.setTo(contactEmail);
//                helper.setSubject("Thông tin đặt vé của bạn - Mã #" + bookingId);
//                helper.setText(emailContent.toString(), true);
//
//                emailSender.send(message);
//                log.info("Đã gửi email thông tin đặt vé tới: {}", contactEmail);
                GmailService.sendEmail(
                        contactEmail,
                        "Cập nhật thông tin - Mã #" + bookingId,
                        emailContent.toString()
                );
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
            } catch (Exception e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void sendUpdateBus(Schedule schedule, Long bookingId, Bus bus) {
        List<BookingInfoDTO> bookingInfoList = bookingService.getBookingInfoList(bookingId);
        Driver driver = schedule.getDriver();
        log.info("Booking info list: {}", bookingInfoList.size());
        if (!bookingInfoList.isEmpty()) {
            String contactEmail = bookingInfoList.get(0).getContactEmail();
            String contactName = bookingInfoList.get(0).getContactName();
            String routeName = bookingInfoList.get(0).getRouteName();
            String departureDate = bookingInfoList.get(0).getDepartureDate();
            String lisensePlate = bookingInfoList.get(0).getLicensePlate();
            String pickupTime = bookingInfoList.get(0).getPickupTime();
            String pickupLocation = bookingInfoList.get(0).getPickupLocation();
            String dropoffLocation = bookingInfoList.get(0).getDropoffLocation();
            String bookingDate = bookingInfoList.get(0).getBookingDate();

            String seatInfo = "";

            for (int i = 0; i < bookingInfoList.size(); i++) {
                BookingInfoDTO info = bookingInfoList.get(i);
                seatInfo += info.getSeatNumber();
                if (i < bookingInfoList.size() - 1) {
                    seatInfo += " , ";
                }
            }

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<div style=\"font-family: 'Arial', sans-serif; max-width: 700px; margin: 0 auto; border: 1px solid #e1e1e1; border-radius: 8px; overflow: hidden; background-color: #f4f4f9;\">");

            // Header
            emailContent.append("<div style=\"background-color: #007BFF; padding: 20px; text-align: center; color: white; border-top-left-radius: 8px; border-top-right-radius: 8px;\">");
            emailContent.append("<h1 style=\"font-size: 24px; margin: 0;\">THÔNG TIN ĐẶT VÉ XE</h1>");
            emailContent.append("<p style=\"font-size: 14px; margin: 5px 0 0;\">Mã đặt vé: #" + bookingId + "</p>");
            emailContent.append("<p style=\"font-size: 12px; margin: 5px 0 0;\">Ngày đặt vé: " + bookingDate + "</p>");
            emailContent.append("</div>");

            // Customer Info
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-bottom: 1px solid #e1e1e1;\">");
            emailContent.append("<h2 style=\"font-size: 18px; color: #333; margin-top: 0;\">Xin chào " + contactName + ",</h2>");
            emailContent.append("<p style=\"color: #666; font-size: 14px;\">TicketGo xin gửi đến bạn thông tin cập nhật <span style='color: #d9534f; font-weight: bold;'>xe</span> cho chuyến đi:</p>");
            emailContent.append("</div>");

            // Trip Details
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff;\">");

            // Route Info
            emailContent.append("<div style=\"margin-bottom: 20px;\">");
            emailContent.append("<h3 style=\"font-size: 16px; color: #333; margin: 0;\">Chuyến đi: " + routeName + "</h3>");
            emailContent.append("<p style=\"color: #666; font-size: 14px;\"> Giờ khởi hành: " + departureDate + "</p>");
            emailContent.append("</div>");

            /// Pickup Info
            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<p style=\"font-size: 14px; color: #888; margin: 0; font-weight: bold;\">ĐIỂM ĐÓN:</p>");
            emailContent.append("<p style=\"font-size: 15px; color: #333; margin: 4px 0 0;\">" + pickupLocation + " • Thời gian đón dự kiến: " + pickupTime + "</p>");
            emailContent.append("</div>");

            // Dropoff Info
            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<p style=\"font-size: 14px; color: #888; margin: 0; font-weight: bold;\">ĐIỂM TRẢ:</p>");
            emailContent.append("<p style=\"font-size: 15px; color: #333; margin: 4px 0 0;\">" + dropoffLocation + "</p>");
            emailContent.append("</div>");


            emailContent.append("</div>");

            // Booking Details
            emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-top: 1px solid #e1e1e1;\">");
            emailContent.append("<h3 style=\"font-size: 16px; color: #333;\">CHI TIẾT ĐẶT VÉ</h3>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Số ghế đã đặt: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + seatInfo + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #2e7d32; font-weight: bold;\">🔄 Biển số xe: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #2e7d32;\">" + bus.getLicensePlate() + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Tên tài xế: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + driver.getName() + "</span>");
            emailContent.append("</div>");

            emailContent.append("<div style=\"margin-bottom: 12px;\">");
            emailContent.append("<span style=\"font-size: 14px; color: #333; font-weight: bold;\">Liên hệ tài xế: </span>");
            emailContent.append("<span style=\"font-size: 14px; color: #333;\">" + driver.getPhoneNumber() + "</span>");
            emailContent.append("</div>");

            emailContent.append("</div>");

            // Status
            emailContent.append("<div style=\"padding: 20px; background-color: #f2f7f4; text-align: center; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
            emailContent.append("<span style=\"background-color: #28a745; color: white; padding: 10px 20px; border-radius: 20px; font-weight: bold;\">✔ ĐÃ XÁC NHẬN</span>");
            emailContent.append("</div>");

            // Footer
            emailContent.append("<div style=\"background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
            emailContent.append("<p style=\"margin: 0 0 10px 0;\">Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này hoặc gọi đến hotline.</p>");
            emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\">Hotline: 0979552239</p>");
            emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\"><a href=\"https://ticketgo-black.vercel.app/\" style=\"color: #007BFF; text-decoration: none;\">TicketGo - Đặt vé xe dễ dàng và nhanh chóng </a></p>");
            emailContent.append("</div>");
            emailContent.append("</div>");

            try {
//                MimeMessage message = emailSender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//                helper.setFrom(fromEmail, fromName);
//                helper.setTo(contactEmail);
//                helper.setSubject("Thông tin đặt vé của bạn - Mã #" + bookingId);
//                helper.setText(emailContent.toString(), true);
//
//                emailSender.send(message);
//                log.info("Đã gửi email thông tin đặt vé tới: {}", contactEmail);
                GmailService.sendEmail(
                        contactEmail,
                        "Cập nhật thông tin - Mã #" + bookingId,
                        emailContent.toString()
                );
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
            } catch (Exception e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    private MimeMessage createResetPasswordEmail(String email, String token)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(email);
        helper.setSubject(getResetPasswordEmailSubject());
        helper.setText(getResetPasswordEmailContent(token), true);

        return message;
    }

    private MimeMessage createActivationEmail(String email, String token)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(email);
        helper.setSubject(getActivationEmailSubject());
        helper.setText(getActivationEmailContent(token), true);

        return message;
    }

    private String getActivationEmailSubject() {
        return "Xác nhận tài khoản của bạn";
    }

    private String getActivationEmailContent(String token) {
        String activationLink = String.format("%s/activate?token=%s", feUrl, token);
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<div style=\"font-family: 'Arial', sans-serif; max-width: 700px; margin: 0 auto; border: 1px solid #e1e1e1; border-radius: 8px; overflow: hidden; background-color: #f4f4f9;\">");

        // Header
        emailContent.append("<div style=\"background-color: #007BFF; padding: 20px; text-align: center; color: white; border-top-left-radius: 8px; border-top-right-radius: 8px;\">");
        emailContent.append("<h1 style=\"font-size: 24px; margin: 0;\">XÁC NHẬN TÀI KHOẢN</h1>");
        emailContent.append("</div>");

        // Customer Info
        emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-bottom: 1px solid #e1e1e1;\">");
        emailContent.append("<h2 style=\"font-size: 18px; color: #333; margin-top: 0;\">Xin chào!" + "</h2>");
        emailContent.append("<p style=\"color: #666; font-size: 14px;\">Cảm ơn bạn đã đăng ký với Ticket Go. Vui lòng nhấn vào <a href=\"")
                .append(activationLink)
                .append("\" style=\"color: #007BFF; text-decoration: none;\">liên kết</a> để xác nhận tài khoản của bạn.</p>");

        emailContent.append("</div>");

        // Footer
        emailContent.append("<div style=\"background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
        emailContent.append("<p style=\"margin: 0 0 10px 0;\">Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này hoặc gọi đến hotline.</p>");
        emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\">Hotline: 0979552239</p>");
        emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\"><a href=\"https://ticketgo-black.vercel.app/\" style=\"color: #007BFF; text-decoration: none;\">TicketGo - Đặt vé xe dễ dàng và nhanh chóng </a></p>");
        emailContent.append("</div>");
        emailContent.append("</div>");

        return emailContent.toString();
    }

    private String getResetPasswordEmailSubject() {
        return "Đặt lại mật khẩu";
    }

    private String getResetPasswordEmailContent(String token) {
        String resetLink = String.format("%s/reset-password?token=%s", feUrl, token);

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<div style=\"font-family: 'Arial', sans-serif; max-width: 700px; margin: 0 auto; border: 1px solid #e1e1e1; border-radius: 8px; overflow: hidden; background-color: #f4f4f9;\">");

        // Header
        emailContent.append("<div style=\"background-color: #007BFF; padding: 20px; text-align: center; color: white; border-top-left-radius: 8px; border-top-right-radius: 8px;\">");
        emailContent.append("<h1 style=\"font-size: 24px; margin: 0;\">ĐẶT LẠI MẬT KHẨU</h1>");
        emailContent.append("</div>");

        // Customer Info
        emailContent.append("<div style=\"padding: 20px; background-color: #ffffff; border-bottom: 1px solid #e1e1e1;\">");
        emailContent.append("<h2 style=\"font-size: 18px; color: #333; margin-top: 0;\">Xin chào!" + "</h2>");
        emailContent.append("<p style=\"color: #666; font-size: 14px;\">Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Ticket Go. Vui lòng nhấn vào <a href=\"")
                .append(resetLink)
                .append("\" style=\"color: #007BFF; text-decoration: none;\">liên kết</a> để đặt lại mật khẩu của bạn.</p>");

        emailContent.append("</div>");

        // Footer
        emailContent.append("<div style=\"background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #999; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;\">");
        emailContent.append("<p style=\"margin: 0 0 10px 0;\">Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này hoặc gọi đến hotline.</p>");
        emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\">Hotline: 0979552239</p>");
        emailContent.append("<p style=\"margin: 0; font-weight: bold; color: #007BFF;\"><a href=\"https://ticketgo-black.vercel.app/\" style=\"color: #007BFF; text-decoration: none;\">TicketGo - Đặt vé xe dễ dàng và nhanh chóng </a></p>");
        emailContent.append("</div>");
        emailContent.append("</div>");

        return emailContent.toString();
    }
}
