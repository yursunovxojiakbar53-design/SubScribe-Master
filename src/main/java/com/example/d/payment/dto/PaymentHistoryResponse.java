package com.example.d.payment.dto;

import com.example.d.subscription.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {
    private Integer id;
    private String subscriptionName;
    private BigDecimal amount;
    private CurrencyType currency;
    private LocalDate paymentDate;
    private BigDecimal exchangeRateAtPayment;
    private BigDecimal amountInBaseCurrency;
}