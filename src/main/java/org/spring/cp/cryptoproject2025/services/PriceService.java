package org.spring.cp.cryptoproject2025.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.dto.PriceDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    public BigDecimal getPrice(String symbol) {
        RestTemplate restTemplate = new RestTemplate();
        String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";
        try {
            PriceDTO priceDTO = restTemplate.getForObject(BINANCE_API_URL + symbol, PriceDTO.class);
            if (priceDTO != null)
                return priceDTO.getPrice();
            else
                return null;
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            return null;
        }
    }

    public String getPriceWithSymbol(String symbol) {
        RestTemplate restTemplate = new RestTemplate();
        String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";
        try {
            PriceDTO priceDTO = restTemplate.getForObject(BINANCE_API_URL + symbol, PriceDTO.class);
            if (priceDTO != null)
                return symbol + " " + priceDTO.getPrice();
            else
                return "error";
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            return "error";
        }
    }
}
