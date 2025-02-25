package org.spring.cp.cryptoproject2025.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceDTO {
    private String symbol;
    private BigDecimal price;
}
