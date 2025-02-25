package org.spring.cp.cryptoproject2025.repositories;

import org.spring.cp.cryptoproject2025.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
}
