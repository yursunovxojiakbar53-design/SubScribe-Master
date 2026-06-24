package com.example.d.analytics.dto;

import java.math.BigDecimal;

/**
 * Bir oylik nuqta - frontendda chart chizish uchun.
 * month - "yyyy-MM" formatida (masalan "2026-06"), Chart.js/Recharts kabi
 * kutubxonalar uchun to'g'ridan-to'g'ri ishlatish mumkin bo'lgan format.
 */
public record MonthlyTrendPoint(
        String month,
        BigDecimal totalInBaseCurrency
) {}