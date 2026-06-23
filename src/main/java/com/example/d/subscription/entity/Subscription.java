package com.example.d.subscription.entity;

import com.example.d.extra.AbstractEntity;
import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.SubscriptionStatus;
import com.example.d.user.entity.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Subscription extends AbstractEntity {

    private String serviceName;

    private BigDecimal amount;

    //keyngi tolov sanasi
    private LocalDate billingDate;

    private SubscriptionStatus status;

    private BillingCycle billingCycle;

    @ManyToOne
    private Users user;
}
