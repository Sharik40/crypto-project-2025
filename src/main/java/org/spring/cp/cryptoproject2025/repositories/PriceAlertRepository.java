package org.spring.cp.cryptoproject2025.repositories;

import org.spring.cp.cryptoproject2025.entities.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
}
