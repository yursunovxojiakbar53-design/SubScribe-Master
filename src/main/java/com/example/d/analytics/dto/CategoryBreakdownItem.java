package com.example.d.analytics.dto;


import com.example.d.analytics.enums.SubscriptionCategory;

import java.math.BigDecimal;

/**
 * Bitta kategoriya bo'yicha (Entertainment, Productivity, ...) oylik xarajat.
 */
public record CategoryBreakdownItem(
        SubscriptionCategory category,
        BigDecimal totalInBaseCurrency,
        int subscriptionsCount
) {}