package com.example.d.notification;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailNotificationService {
    private final JavaMailSender mailSender;


    public void send(String toEmail, String message) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setSubject("Subscription Reminder");
        mail.setText(message);

        mailSender.send(mail);
    }
}
