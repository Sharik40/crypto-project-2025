package org.spring.cp.cryptoproject2025.services;

import lombok.RequiredArgsConstructor;
import org.spring.cp.cryptoproject2025.entities.Crypto;
import org.spring.cp.cryptoproject2025.entities.User;
import org.spring.cp.cryptoproject2025.repositories.CryptoRepository;
import org.spring.cp.cryptoproject2025.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<Crypto> getAllCryptos(Long chatId) {
        User user = userRepository.findByChatId(chatId);
        return user.getCryptos();
    }

    @Transactional
    public Crypto findCryptoByChatId(Long chatId, String symbol) {
        return getAllCryptos(chatId).stream().filter(crypto -> crypto.getSymbol().equals(symbol)).findFirst().orElse(null);
    }

    @Transactional
    public boolean addCrypto(String symbol, Long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (findCryptoByChatId(chatId, symbol) != null) {
            return false;
        } else {
            Crypto crypto = new Crypto();
            crypto.setSymbol(symbol);
            System.out.println(symbol);
            crypto.setUser(user);
            cryptoRepository.save(crypto);
            return true;
        }
    }
}
