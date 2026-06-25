package com.example.d.payment.service;

import com.example.d.currency.service.CurrencyService;
import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.payment.PaymentHistory;
import com.example.d.payment.dto.PaymentHistoryResponse;
import com.example.d.payment.mapper.PaymentHistoryMapper;
import com.example.d.payment.repository.PaymentHistoryRepository;
import com.example.d.security.CustomUserDetails;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.repository.SubscriptionRepository;
import com.example.d.user.entity.Users;
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
    private final CurrencyService currencyService;


    /**
     * To'lovni (yoki uning simulyatsiyasini) PaymentHistory'ga yozadi.
     * Summa va valyuta obunaning O'Z valyutasida saqlanadi; qo'shimcha
     * ravishda o'sha paytdagi kurs va asosiy valyutadagi ekvivalenti
     * hisoblanib qo'yiladi, shunda tarix keyingi kurs o'zgarishidan ta'sirlanmaydi.
     */
    @Transactional
    public void recordPayment(Subscription subscription, LocalDate paymentDate) {
        Users user = subscription.getUser();
        CurrencyType baseCurrency = user.getPreferredCurrency();

        BigDecimal exchangeRate = currencyService.convert(
                BigDecimal.ONE, subscription.getCurrency(), baseCurrency);
        BigDecimal amountInBaseCurrency = currencyService.convert(
                subscription.getAmount(), subscription.getCurrency(), baseCurrency);

        PaymentHistory history = new PaymentHistory();
        history.setSubscription(subscription);
        history.setAmount(subscription.getAmount());
        history.setCurrency(subscription.getCurrency());
        history.setPaymentDate(paymentDate);
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
