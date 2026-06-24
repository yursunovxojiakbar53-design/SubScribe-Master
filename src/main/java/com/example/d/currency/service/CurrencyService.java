package com.example.d.currency.service;

import com.example.d.currency.client.CbuApiClient;
import com.example.d.currency.dto.CurrencyRateDto;
import com.example.d.subscription.enums.CurrencyType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CbuApiClient cbuApiClient;

    // Circuit Breaker ishlamay qolganda ishlatiladigan, oxirgi muvaffaqiyatli kelgan natija
    private volatile Map<String, BigDecimal> lastKnownRates = new ConcurrentHashMap<>();

    /**
     * Barcha valyutalar kursini cbu.uz'dan olib keladi, 24 soat keshlanadi.
     * Agar API ishlamasa (timeout/500), getFallbackRates() chaqiriladi.
     */
    @Cacheable("currency-rates")
    @CircuitBreaker(name = "debugApi", fallbackMethod = "getFallbackRates")
    public Map<String, BigDecimal> getRates() {
        List<CurrencyRateDto> rates = cbuApiClient.getRates();

        Map<String, BigDecimal> result = rates.stream()
                .collect(Collectors.toMap(
                        CurrencyRateDto::ccy,
                        rate -> new BigDecimal(rate.rate())
                ));

        lastKnownRates = result; // muvaffaqiyatli bo'lsa, fallback uchun saqlab qo'yamiz
        return result;
    }

    /**
     * Fallback metod - imzosi asosiy metodga mos bo'lishi shart (+ Throwable parametri).
     */
    private Map<String, BigDecimal> getFallbackRates(Throwable t) {
        log.warn("CBU API ishlamadi, oxirgi keshlangan kurslardan foydalanilmoqda. Sabab: {}", t.getMessage());
        if (lastKnownRates.isEmpty()) {
            throw new RuntimeException("Valyuta kursi mavjud emas va keshda ham yo'q", t);
        }
        return lastKnownRates;
    }


    public BigDecimal getRateToUzs(CurrencyType currency) {
        if (currency == CurrencyType.UZS) {
            return BigDecimal.ONE;
        }
        BigDecimal rate = getRates().get(currency.name());
        if (rate == null) {
            throw new RuntimeException(currency + " kursi topilmadi");
        }
        return rate;
    }

    public BigDecimal convert(BigDecimal amount, CurrencyType from, CurrencyType to) {
        if (from == to) {
            return amount;
        }
        BigDecimal amountInUzs = amount.multiply(getRateToUzs(from));

        if (to == CurrencyType.UZS) {
            return amountInUzs.setScale(2, RoundingMode.HALF_UP);
        }
        return amountInUzs.divide(getRateToUzs(to), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getUsdRate() {
        return getRateToUzs(CurrencyType.USD);
    }
}