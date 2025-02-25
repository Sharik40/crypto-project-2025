package org.spring.cp.cryptoproject2025.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.dto.PriceDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    public String getPrice(String symbol) {
        RestTemplate restTemplate = new RestTemplate();
        String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";
        try {
            PriceDTO priceDTO = restTemplate.getForObject(BINANCE_API_URL + symbol, PriceDTO.class);
            if (priceDTO != null)
                return "Поточна ціна " + symbol + " : " + priceDTO.getPrice().toString();
            else
                return "Wrong symbol";
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            return "Error";
        }
    }
}
