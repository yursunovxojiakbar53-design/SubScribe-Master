package com.example.d.subscription.dto;

import java.math.BigDecimal;

public record SubscriptionResponse(Integer id, String name, BigDecimal price) {}