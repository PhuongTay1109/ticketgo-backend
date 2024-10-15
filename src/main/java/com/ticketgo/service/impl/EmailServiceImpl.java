package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;

    @Async
    @Override
    public void sendActivationEmail(String email, String token) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("contact@ticketgo.com", "Hỗ trợ Ticket Go");
            helper.setTo(email);

            String subject = "Xác nhận tài khoản của bạn";
            String activationLink = "http://localhost:3000/activate?token=" + token;

            String content = "<p>Xin chào,</p>" +
                    "<p>Cảm ơn bạn đã đăng ký với Ticket Go.</p>" +
                    "<p>Vui lòng nhấn vào liên kết bên dưới để xác nhận tài khoản của bạn:</p>" +
                    "<p><a href=\"" + activationLink + "\">Xác nhận tài khoản của tôi</a></p>";

            helper.setSubject(subject);
            helper.setText(content, true);

        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new AppException("Gửi email không thành công", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        emailSender.send(message);
        CompletableFuture.completedFuture(null);
    }

}
