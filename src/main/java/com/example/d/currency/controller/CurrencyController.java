package com.example.d.currency.controller;

import com.example.d.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/currency/usd")
    public BigDecimal usdRate() {
        return currencyService.getUsdRate();
    }

}