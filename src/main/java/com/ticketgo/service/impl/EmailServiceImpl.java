package com.ticketgo.service.impl;

import com.ticketgo.dto.BookingInfoDTO;
import com.ticketgo.entity.Driver;
import com.ticketgo.entity.Schedule;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.EmailService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    private BookingService bookingService;
    private ScheduleService scheduleService;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            @Lazy BookingService bookingService,
                            @Lazy ScheduleService scheduleService) {
        this.emailSender = emailSender;
        this.bookingService = bookingService;
        this.scheduleService = scheduleService;
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
            MimeMessage message = createActivationEmail(email, token);
            emailSender.send(message);
            return CompletableFuture.completedFuture(true);
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Failed to send activation email to {}: {}", email, e.getMessage());
            return CompletableFuture.completedFuture(false);
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

            String seatInfo = "";

            for (int i = 0; i < bookingInfoList.size(); i++) {
                BookingInfoDTO info = bookingInfoList.get(i);
                seatInfo += info.getSeatNumber() + " (Mã vé: " + info.getTicketCode() + ")";
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
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail, fromName);
                helper.setTo(contactEmail);
                helper.setSubject("Thông tin đặt vé của bạn - Mã #" + bookingId);
                helper.setText(emailContent.toString(), true);

                emailSender.send(message);
                log.info("Đã gửi email thông tin đặt vé tới: {}", contactEmail);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Không thể gửi email thông tin đặt vé tới {}: {}", contactEmail, e.getMessage());
            }
        }
    }

    @Async("taskExecutor")
    @Override
    public void sendResetPasswordEmail(String email, String token) {
        try {
            MimeMessage message = createResetPasswordEmail(email, token);
            emailSender.send(message);
            log.info("Đã gửi email đặt lại mật khẩu tới: {}", email);
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Failed to send activation email to {}: {}", email, e.getMessage());
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
