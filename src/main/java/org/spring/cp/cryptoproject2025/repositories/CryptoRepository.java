package org.spring.cp.cryptoproject2025.repositories;

import org.spring.cp.cryptoproject2025.entities.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {
    Crypto findBySymbol(String symbol);
}
