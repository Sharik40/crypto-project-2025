package org.spring.cp.cryptoproject2025.config;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.bot.TelegramBot;
import org.spring.cp.cryptoproject2025.services.KeyboardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
//@RequiredArgsConstructor
public class TelegramConfig {




    @Bean
    public TelegramBot telegramBot(@Value("${bot.username}") String botName,
                                   @Value("${bot.token}") String botToken,
                                   KeyboardService keyboardService) {
        TelegramBot telegramBot = new TelegramBot(botName, botToken, keyboardService);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Exception during registration telegram api: {}", e.getMessage());
        }
        return telegramBot;
    }
}
