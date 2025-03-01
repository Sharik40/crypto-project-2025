package org.spring.cp.cryptoproject2025.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.dto.UserDTO;
import org.spring.cp.cryptoproject2025.dto.UserStates;
import org.spring.cp.cryptoproject2025.entities.User;
import org.spring.cp.cryptoproject2025.mappers.UserMapper;
import org.spring.cp.cryptoproject2025.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Transactional
    public void setUserState(Long chatId, UserStates state) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {
            user.setUserState(state);
            userRepository.save(user);
        }
    }

    public UserStates getUserState(Long chatId) {
        User user = userRepository.findByChatId(chatId);
        return user != null ? user.getUserState() : UserStates.DEFAULT;
    }

    public void setTargetSymbol(Long chatId, String targetSymbol) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {
            user.setTargetSymbol(targetSymbol);
            userRepository.save(user);
        }
    }

    public String getTargetSymbol(Long chatId) {
        User user = userRepository.findByChatId(chatId);
        return user != null ? user.getTargetSymbol() : null;
    }
}
