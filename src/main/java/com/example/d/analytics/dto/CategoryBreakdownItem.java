package com.example.d.analytics.dto;


import com.example.d.analytics.enums.SubscriptionCategory;

import java.math.BigDecimal;

/**
 * Bitta kategoriya bo'yicha oylik xarajat
 */
public record CategoryBreakdownItem(
        SubscriptionCategory category,
        BigDecimal totalInBaseCurrency,
        int subscriptionsCount
) {}