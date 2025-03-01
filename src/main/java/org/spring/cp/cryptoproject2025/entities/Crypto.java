package org.spring.cp.cryptoproject2025.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "favorite_crypto")
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
