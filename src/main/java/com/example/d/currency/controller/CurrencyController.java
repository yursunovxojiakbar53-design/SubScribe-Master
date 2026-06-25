package com.example.d.currency.controller;

import com.example.d.currency.service.CurrencyService;
import com.example.d.extra.Perms;
import com.example.d.extra.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Tag(name = "Currency", description = "Valyuta kurslari")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/currency/usd")
    @RequirePermission(Perms.CURRENCY_READ)
    @Operation(summary = "USD → UZS kursi")
    public BigDecimal usdRate() {
        return currencyService.getUsdRate();
    }

}