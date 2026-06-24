package com.example.d.analytics.dto;

import com.example.d.subscription.enums.CurrencyType;

import java.math.BigDecimal;

public record MostExpensiveSubscriptionResponse(
        String serviceName,
        BigDecimal amountInBaseCurrency,
        CurrencyType baseCurrency
) {}