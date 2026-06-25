package com.example.d.analytics.dto;

import com.example.d.subscription.enums.CurrencyType;

import java.math.BigDecimal;

/*
    userni oylik sarf-xarajatlari haqida ma'lumot beradi(aktivlari)
     */
public record MonthlySpendingResponse(
        BigDecimal totalInBaseCurrency,
        CurrencyType baseCurrency,
        int activeSubscriptionsCount
) {}