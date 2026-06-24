package com.example.d.subscription.dto;

import com.example.d.analytics.enums.SubscriptionCategory;
import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public SubscriptionCreateRequest(String serviceName, BigDecimal price, CurrencyType currency, BillingCycle billingCycle, LocalDate startDate, SubscriptionCategory subscriptionCategory) {
        this.serviceName = serviceName;
        this.price = price;
        this.currency = currency;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.subscriptionCategory = subscriptionCategory;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public SubscriptionCategory getSubscriptionCategory() {
        return subscriptionCategory;
    }
}
