package com.example.d.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogNotificationService implements NotificationService {
    @Override
    public void send(String toEmail, String message) {
        log.info("Xabar {} manziliga LOG orqali yuborildi. Matni: {}", toEmail, message);    }
}