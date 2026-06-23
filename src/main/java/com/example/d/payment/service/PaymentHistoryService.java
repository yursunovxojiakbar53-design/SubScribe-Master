package com.example.d.payment.service;

import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.payment.PaymentHistory;
import com.example.d.payment.dto.PaymentHistoryResponse;
import com.example.d.payment.mapper.PaymentHistoryMapper;
import com.example.d.payment.repository.PaymentHistoryRepository;
import com.example.d.security.CustomUserDetails;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;


    @Transactional
    public void recordPayment(Subscription subscription, BigDecimal exchangeRate, BigDecimal amountInBaseCurrency) {
        PaymentHistory history = new PaymentHistory();
        history.setSubscription(subscription);
        history.setAmount(subscription.getAmount());
        history.setCurrency(subscription.getUser().getPreferredCurrency());
        history.setPaymentDate(LocalDate.now());
        history.setExchangeRateAtPayment(exchangeRate);
        history.setAmountInBaseCurrency(amountInBaseCurrency);

        paymentHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public ApiResponse getHistoryBySubscription(Integer subscriptionId, Authentication authentication) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundException("Subscription not found"));

        String username = getUsername(authentication);
        if (!subscription.getUser().getUsername().equals(username)) {
            throw new ForbiddenException("User not allowed to view this history");
        }
        List<PaymentHistoryResponse> history = paymentHistoryRepository
                .findBySubscription_IdOrderByPaymentDateDesc(subscriptionId)
                .stream()
                .map(paymentHistoryMapper::toResponse)
                .toList();

        return new ApiResponse("Payment history retrieved successfully", true, history);
    }


    private String getUsername(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Invalid user");
        }
        return userDetails.getUsername();
    }
}
