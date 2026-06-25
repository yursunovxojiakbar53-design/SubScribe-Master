package com.example.d.analytics.dto;

import java.math.BigDecimal;

/**
 * Bir oylik nuqta  frontendda chart chizish uchun
 */
public record MonthlyTrendPoint(
        String month,
        BigDecimal totalInBaseCurrency
) {}