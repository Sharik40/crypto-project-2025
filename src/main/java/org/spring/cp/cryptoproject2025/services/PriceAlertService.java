package org.spring.cp.cryptoproject2025.services;

import lombok.RequiredArgsConstructor;
import org.spring.cp.cryptoproject2025.dto.AlertType;
import org.spring.cp.cryptoproject2025.dto.PriceAlertDTO;
import org.spring.cp.cryptoproject2025.entities.PriceAlert;
import org.spring.cp.cryptoproject2025.mappers.PriceAlertMapper;
import org.spring.cp.cryptoproject2025.repositories.PriceAlertRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceAlertService {
    private final PriceAlertRepository priceAlertRepository;

    private final PriceService priceService;

    private final PriceAlertMapper priceAlertMapper;

    public void addAlert(Long chatId, String symbol, BigDecimal targetPrice) {
        AlertType alertType;
        if (targetPrice.doubleValue() < priceService.getPrice(symbol).doubleValue()) {
            alertType = AlertType.FALL;
        } else {
            alertType = AlertType.RISE;
        }
        PriceAlertDTO priceAlertDTO = PriceAlertDTO.builder()
                .symbol(symbol)
                .targetPrice(targetPrice)
                .chatId(chatId)
                .alertType(alertType)
                .build();
        priceAlertRepository.save(priceAlertMapper.toPriceAlert(priceAlertDTO));
    }

    public List<PriceAlert> getAllAlerts() {
        return priceAlertRepository.findAll();
    }

    public void deleteAlert(PriceAlert priceAlert) {
        priceAlertRepository.delete(priceAlert);
    }
}
