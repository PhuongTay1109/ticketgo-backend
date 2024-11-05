package com.ticketgo.service.impl;

import com.ticketgo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.fromName}")
    private String fromName;

    @Value("${app.url.base}")
    private String baseUrl;

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
        String activationLink = String.format("%s/activate?token=%s", baseUrl, token);
        return """
               <p>Xin chào,</p>
               <p>Cảm ơn bạn đã đăng ký với Ticket Go.</p>
               <p>Vui lòng nhấn vào liên kết bên dưới để xác nhận tài khoản của bạn:</p>
               <p><a href="%s">Xác nhận tài khoản của tôi</a></p>
               """.formatted(activationLink);
    }
}
