package com.example.d.analytics.dto;

import com.example.d.subscription.enums.CurrencyType;

import java.math.BigDecimal;


public record MonthlySpendingResponse(
        BigDecimal totalInBaseCurrency,
        CurrencyType baseCurrency,
        int activeSubscriptionsCount
) {}