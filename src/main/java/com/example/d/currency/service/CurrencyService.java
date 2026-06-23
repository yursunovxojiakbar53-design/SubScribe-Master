package com.example.d.currency.service;

import com.example.d.currency.client.CbuApiClient;
import com.example.d.currency.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CbuApiClient cbuApiClient;

    public BigDecimal getUsdRate() {

        return cbuApiClient.getRates()
                .stream()
                .filter(rate -> "USD".equals(rate.ccy()))
                .findFirst()
                .map(rate -> new BigDecimal(rate.rate())).orElseThrow(() -> new RuntimeException("USD rate not found"));
    }


    @Cacheable("currency-rates")
    public List<CurrencyRateDto> getRates() {
        return cbuApiClient.getRates();
    }
}