package com.example.d.payment;

import com.example.d.extra.AbstractEntity;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class PaymentHistory extends AbstractEntity {



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    private LocalDate paymentDate;

    //Tolov paytidagi kurs
    private BigDecimal exchangeRateAtPayment;

    //shu kurs boyicha hisoblanadi
    private BigDecimal amountInBaseCurrency;

    @CreatedDate
    private LocalDateTime createdDate;
}