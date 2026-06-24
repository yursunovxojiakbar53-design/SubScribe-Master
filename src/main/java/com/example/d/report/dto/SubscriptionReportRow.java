package com.example.d.report.dto;

import com.example.d.subscription.enums.CurrencyType;

import java.math.BigDecimal;

public record SubscriptionReportRow(
        String serviceName,
        BigDecimal monthlyPrice,
        CurrencyType currency,
        CurrencyType baseCurrency,
        //user currencyTypega qarab hisoblangan summa
        BigDecimal  equivalentInBaseCurrency,
        BigDecimal monthlyTotalInBaseCurrency,

        BigDecimal yearlyTotalInBaseCurrency
) {}