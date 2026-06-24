package com.example.d.subscription.entity;

import com.example.d.analytics.enums.SubscriptionCategory;
import com.example.d.extra.AbstractEntity;
import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.enums.SubscriptionStatus;
import com.example.d.user.entity.Users;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "subscription")
public class Subscription extends AbstractEntity {

    private String serviceName;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate setNextPaymentDate;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    @Enumerated(EnumType.STRING)
    private SubscriptionCategory subscriptionCategory;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    private Boolean isDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    public Subscription() {
    }

    public Subscription(String serviceName, BigDecimal amount, LocalDate startDate, LocalDate setNextPaymentDate,
                        CurrencyType currency, SubscriptionCategory subscriptionCategory, SubscriptionStatus status,
                        BillingCycle billingCycle, Boolean isDelete, Users user) {
        this.serviceName = serviceName;
        this.amount = amount;
        this.startDate = startDate;
        this.setNextPaymentDate = setNextPaymentDate;
        this.currency = currency;
        this.subscriptionCategory = subscriptionCategory;
        this.status = status;
        this.billingCycle = billingCycle;
        this.isDelete = isDelete;
        this.user = user;
    }

    protected Subscription(Builder builder) {
        super(builder);
        this.serviceName = builder.serviceName;
        this.amount = builder.amount;
        this.startDate = builder.startDate;
        this.setNextPaymentDate = builder.setNextPaymentDate;
        this.currency = builder.currency;
        this.subscriptionCategory = builder.subscriptionCategory;
        this.status = builder.status;
        this.billingCycle = builder.billingCycle;
        this.isDelete = builder.isDelete;
        this.user = builder.user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getSetNextPaymentDate() {
        return setNextPaymentDate;
    }

    public void setSetNextPaymentDate(LocalDate setNextPaymentDate) {
        this.setNextPaymentDate = setNextPaymentDate;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public SubscriptionCategory getSubscriptionCategory() {
        return subscriptionCategory;
    }

    public void setSubscriptionCategory(SubscriptionCategory subscriptionCategory) {
        this.subscriptionCategory = subscriptionCategory;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + getId() +
                ", serviceName='" + serviceName + '\'' +
                ", amount=" + amount +
                ", startDate=" + startDate +
                ", setNextPaymentDate=" + setNextPaymentDate +
                ", currency=" + currency +
                ", subscriptionCategory=" + subscriptionCategory +
                ", status=" + status +
                ", billingCycle=" + billingCycle +
                ", isDelete=" + isDelete +
                '}';
    }

    public static class Builder extends AbstractEntity.Builder<Subscription, Builder> {
        private String serviceName;
        private BigDecimal amount;
        private LocalDate startDate;
        private LocalDate setNextPaymentDate;
        private CurrencyType currency;
        private SubscriptionCategory subscriptionCategory;
        private SubscriptionStatus status;
        private BillingCycle billingCycle;
        private Boolean isDelete;
        private Users user;

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setNextPaymentDate(LocalDate setNextPaymentDate) {
            this.setNextPaymentDate = setNextPaymentDate;
            return this;
        }

        public Builder currency(CurrencyType currency) {
            this.currency = currency;
            return this;
        }

        public Builder subscriptionCategory(SubscriptionCategory subscriptionCategory) {
            this.subscriptionCategory = subscriptionCategory;
            return this;
        }

        public Builder status(SubscriptionStatus status) {
            this.status = status;
            return this;
        }

        public Builder billingCycle(BillingCycle billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public Builder isDelete(Boolean isDelete) {
            this.isDelete = isDelete;
            return this;
        }

        public Builder user(Users user) {
            this.user = user;
            return this;
        }

        @Override
        public Subscription build() {
            return new Subscription(this);
        }
    }
}