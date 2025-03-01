package org.spring.cp.cryptoproject2025.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.spring.cp.cryptoproject2025.dto.AlertType;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "price_alert")
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String symbol;
    private BigDecimal targetPrice;
    private AlertType alertType;
}
