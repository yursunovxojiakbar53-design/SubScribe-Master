package com.example.d.analytics.dto;

/**
 * Tizimdagi barcha foydalanuvchilar bo'yicha eng ko'p ishlatiladigan
 * xizmatlar reytingining bitta qatori — faqat admin uchun.
 */
public record PopularServiceItem(
        String serviceName,
        Long subscribersCount
) {}
