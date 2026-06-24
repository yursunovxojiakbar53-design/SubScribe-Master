package com.example.d.subscription.dto;

import com.example.d.analytics.enums.SubscriptionCategory;
import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class SubscriptionCreateRequest {
    @NotBlank(message = "Obuna nomi bo'sh bo'lishi mumkin emas")
    private String serviceName;

    @NotNull(message = "Narx kiritilishi shart")
    private BigDecimal price;

    @NotNull(message = "Valyuta tanlanishi shart")
    private CurrencyType currency;

    @NotNull(message = "Davriylik tanlanishi shart")
    private BillingCycle billingCycle;

    @NotNull(message = "Boshlanish sanasi kiritilishi shart")
    private LocalDate startDate;

    private SubscriptionCategory subscriptionCategory;
}
