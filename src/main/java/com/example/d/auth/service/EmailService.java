package com.example.d.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;


    public void sendVerificationEmail(String toEmail, Integer code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verification Email");
        message.setText("Tasdiqlash kodingiz: " + code +
                "\n\nQuyidagi linkka o'ting: " +
                "http://localhost:8080/api/auth/verify?email=" + toEmail + "&code=" + code);

        sendSafely(message, toEmail, "verification");
    }

    private void sendSafely(SimpleMailMessage message, String toEmail, String emailType) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (isBlank(toEmail)) {
            log.warn("Email not sent: recipient is empty for {} email.", emailType);
            return;
        }

        if (!isMailConfigured(mailSender)) {
            log.error("Email not sent to {}: mail sender is not configured. Set spring.mail.* or MAIL_USERNAME and MAIL_PASSWORD.", toEmail);
            return;
        }

        try {
            mailSender.send(message);
            log.info("{} email sent to {}", emailType, toEmail);
        } catch (MailException ex) {
            log.error("Failed to send {} email to {}: {}", emailType, toEmail, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error while sending {} email to {}: {}", emailType, toEmail, ex.getMessage(), ex);
        }
    }


    private boolean isMailConfigured(JavaMailSender mailSender) {
        if (mailSender == null) {
            return false;
        }

        if (mailSender instanceof JavaMailSenderImpl javaMailSender) {
            return !isBlank(javaMailSender.getUsername()) && !isBlank(javaMailSender.getPassword());
        }
        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
