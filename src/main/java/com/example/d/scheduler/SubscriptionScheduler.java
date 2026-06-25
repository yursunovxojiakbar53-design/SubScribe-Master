package com.example.d.scheduler;

import com.example.d.notification.EmailNotificationService;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.service.SubscriptionService;
import com.example.d.user.entity.Users;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;
    private final EmailNotificationService emailNotificationService;

    public SubscriptionScheduler(SubscriptionService subscriptionService, EmailNotificationService emailNotificationService) {
        this.subscriptionService = subscriptionService;
        this.emailNotificationService = emailNotificationService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    @SchedulerLock(name = "SubscriptionScheduler_sendPaymentReminders", lockAtMostFor = "15m", lockAtLeastFor = "5m")
    public void sendPaymentReminders() {
        List<Subscription> subscriptions = subscriptionService.findSubscriptionsWithPaymentInDays(2);
        for (Subscription subscription : subscriptions) {
            Users user = subscription.getUser();
            if (user != null && user.isNotificationEnabled()) {
                emailNotificationService.send( buildMessage(subscription),user.getEmail());
            }
        }
    }

    /**
     * Har kuni 00:30 da to'lov muddati kelgan aktiv obunalar uchun to'lov
     * simulyatsiya qilinadi: PaymentHistory'ga yoziladi va keyingi to'lov sanasi suriladi.
     */
    @Scheduled(cron = "0 30 0 * * ?")
    @SchedulerLock(name = "SubscriptionScheduler_processDuePayments", lockAtMostFor = "15m", lockAtLeastFor = "1m")
    public void processDuePayments() {
        subscriptionService.processDuePayments();
    }

    private String buildMessage(Subscription subscription) {
        return "Hello, your subscription '" + subscription.getServiceName() + "' will expire in 2 days. Please prepare payment.";
    }
}