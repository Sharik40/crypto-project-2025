package org.spring.cp.cryptoproject2025.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.spring.cp.cryptoproject2025.dto.UserStates;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String userName;

    private String language;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Crypto> cryptos;

    private UserStates userState;

    private String targetSymbol;
}
