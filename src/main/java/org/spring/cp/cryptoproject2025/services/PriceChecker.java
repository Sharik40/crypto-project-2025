package org.spring.cp.cryptoproject2025.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.bot.TelegramBot;
import org.spring.cp.cryptoproject2025.dto.AlertType;
import org.spring.cp.cryptoproject2025.entities.PriceAlert;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class PriceChecker {
    private final PriceService priceService;
    private final PriceAlertService priceAlertService;
    private final TelegramBot telegramBot;
    private final TranslationService translationService;
    private final UserService userService;
    private final KeyboardService keyboardService;

    public String translate(String key, String language) {
        return translationService.getMessage(key, language);
    }

    @Scheduled(fixedRate = 5000) // Запуск каждые 10 секунд
    public void checkPrices() {
        List<PriceAlert> alerts = priceAlertService.getAllAlerts();

        Map<String, BigDecimal> priceCache = new ConcurrentHashMap<>();

        alerts.parallelStream().forEach(alert -> {
            String symbol = alert.getSymbol();

            BigDecimal currentPrice = priceCache.computeIfAbsent(symbol, priceService::getPrice);

            if (currentPrice.compareTo(BigDecimal.ZERO) > 0 && isPriceMet(alert, currentPrice)) {
                sendNotification(alert.getChatId(), symbol, alert.getTargetPrice(), currentPrice);
                priceAlertService.deleteAlert(alert); // Удаляем сработавший alert
            }
        });
    }

    private boolean isPriceMet(PriceAlert alert, BigDecimal currentPrice) {
        BigDecimal targetPrice = alert.getTargetPrice();

//        log.info("🔍 Проверка {} | Текущая цена: {} | Целевая цена: {} | Тип алерта: {}",
//                alert.getSymbol(), currentPrice, alert.getTargetPrice(), alert.getAlertType());
        if (alert.getAlertType() == AlertType.RISE) {
            return currentPrice.compareTo(targetPrice) >= 0;
        }
        else if (alert.getAlertType() == AlertType.FALL) {
            return currentPrice.compareTo(targetPrice) <= 0;
        }
        return false;

    }

    private void sendNotification(Long chatId, String symbol, BigDecimal targetPrice, BigDecimal currentPrice) {
        String language = userService.getUserLanguage(chatId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(String.format(translate("alert_success", language), symbol, targetPrice));
        message.setReplyMarkup(keyboardService.backToMenu(language));
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
