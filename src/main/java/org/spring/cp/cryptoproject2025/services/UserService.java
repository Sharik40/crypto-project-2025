package org.spring.cp.cryptoproject2025.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.dto.UserDTO;
import org.spring.cp.cryptoproject2025.entities.User;
import org.spring.cp.cryptoproject2025.mappers.UserMapper;
import org.spring.cp.cryptoproject2025.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public void saveUser (UserDTO userDTO) {
        if (userRepository.findByChatId(userDTO.chatId()) == null) {
            userRepository.save(userMapper.toUser(userDTO));
        }
    }

    public void saveLanguage (Long chatId, String language) {
        User user = userRepository.findByChatId(chatId);
        user.setLanguage(language);
        userRepository.save(user);
    }

    public User getUserByChatId (Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public String getUserLanguage (Long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user == null)
            return null;
        return user.getLanguage();
    }
}
