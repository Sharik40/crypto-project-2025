package org.spring.cp.cryptoproject2025.mappers;

import org.mapstruct.Mapper;
import org.spring.cp.cryptoproject2025.dto.PriceAlertDTO;
import org.spring.cp.cryptoproject2025.entities.PriceAlert;

@Mapper(componentModel = "spring")
public interface PriceAlertMapper {
    PriceAlert toPriceAlert(PriceAlertDTO priceAlertDTO);
}
