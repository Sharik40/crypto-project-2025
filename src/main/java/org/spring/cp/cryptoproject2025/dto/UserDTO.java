package org.spring.cp.cryptoproject2025.dto;

import lombok.Builder;

@Builder
public record UserDTO (
         Long chatId,

         String userName,

         String language
){

}
