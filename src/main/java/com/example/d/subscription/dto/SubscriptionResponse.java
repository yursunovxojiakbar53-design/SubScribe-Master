package com.example.d.subscription.dto;

import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionResponse(
        Integer id,
        String serviceName,
        BigDecimal price,
        CurrencyType currency,
        BigDecimal equivalentInBaseCurrency,
        LocalDate startDate,
        LocalDate nextPaymentDate,
        BillingCycle billingCycle,
        SubscriptionStatus status
) {}