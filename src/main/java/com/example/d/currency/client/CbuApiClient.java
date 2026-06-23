package com.example.d.currency.client;

import com.example.d.currency.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CbuApiClient {

    private final RestClient restClient;

    private static final String URL = "https://cbu.uz/uz/arkhiv-kursov-valyut/json/";

    public List<CurrencyRateDto> getRates() {

        CurrencyRateDto[] response =
                restClient.get()
                        .uri(URL)
                        .retrieve()
                        .body(CurrencyRateDto[].class);

        assert response != null;
        return Arrays.asList(response);
    }
}