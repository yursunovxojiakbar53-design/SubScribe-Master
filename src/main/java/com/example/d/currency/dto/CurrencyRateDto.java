package com.example.d.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CurrencyRateDto(
        @JsonProperty("Ccy")
        String ccy,

        @JsonProperty("Rate")
        String rate,

        @JsonProperty("Date")
        String date
){
}