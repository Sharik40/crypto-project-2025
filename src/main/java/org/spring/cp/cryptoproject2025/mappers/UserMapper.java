package org.spring.cp.cryptoproject2025.mappers;

import org.mapstruct.Mapper;
import org.spring.cp.cryptoproject2025.dto.UserDTO;
import org.spring.cp.cryptoproject2025.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDTO userDTO);
    UserDTO toUserDTO(User user);
}
