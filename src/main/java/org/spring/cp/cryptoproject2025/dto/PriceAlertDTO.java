package org.spring.cp.cryptoproject2025.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PriceAlertDTO(
        Long chatId,
        String symbol,
        BigDecimal targetPrice,
        AlertType alertType
) {
}
