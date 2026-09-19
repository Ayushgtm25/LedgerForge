package com.spendsmart.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:noreply@spendsmart.com}")
    private String fromEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String to, String title, String otpCode) {
        log.info("Sending OTP email to {}", to);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(title);

            String htmlContent = buildOtpEmailContent(title, otpCode);
            helper.setText(htmlContent, true); // true indicates HTML

            javaMailSender.send(message);
            log.info("OTP email successfully sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
        }
    }

    public void sendBroadcastEmail(String to, String title, String bodyText) {
        log.info("Sending broadcast email to {}", to);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(title);
            String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">"
                    + "<h2 style=\"color: #2e6c80;\">SpendSmart</h2>"
                    + "<p style=\"font-size: 16px; color: #333;\">" + escapeHtml(title) + "</p>"
                    + "<p style=\"font-size: 15px; color: #444; white-space: pre-wrap;\">" + escapeHtml(bodyText) + "</p>"
                    + "</div>";
            helper.setText(html, true);

            javaMailSender.send(message);
            log.info("Broadcast email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send broadcast email to {}: {}", to, e.getMessage());
        }
    }

    private static String escapeHtml(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String buildOtpEmailContent(String title, String otpCode) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
                "<h2 style=\"color: #2e6c80;\">SpendSmart Security</h2>" +
                "<p style=\"font-size: 16px; color: #333;\">" + title + "</p>" +
                "<div style=\"text-align: center; margin: 30px 0;\">" +
                "<span style=\"font-size: 32px; font-weight: bold; letter-spacing: 4px; color: #1a73e8; background-color: #f0f4f8; padding: 15px 30px; border-radius: 8px;\">" + otpCode + "</span>" +
                "</div>" +
                "<p style=\"font-size: 14px; color: #666;\">This code is valid for 5 minutes. Please do not share it with anyone.</p>" +
                "</div>";
    }
}
