package org.spring.cp.cryptoproject2025.bot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.dto.UserDTO;
import org.spring.cp.cryptoproject2025.services.KeyboardService;
import org.spring.cp.cryptoproject2025.services.PriceService;
import org.spring.cp.cryptoproject2025.services.TranslationService;
import org.spring.cp.cryptoproject2025.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class TelegramBot extends TelegramLongPollingBot {

    private String botName;

    private final String startText = "Привіт\n\nЦей бот може надати поточну ціну криптовалюти\n\nНадішліть боту `/price` та бажану криптовалютну пару у форматі `BTCUSDT`";

    @Autowired
    private PriceService priceService;

    @Autowired
    private UserService userService;

    @Autowired
    private TranslationService translationService;

    private final KeyboardService keyboardService;

    public TelegramBot(String botName, String botToken, KeyboardService keyboardService) {
        super(botToken);
        this.botName = botName;
        this.keyboardService = keyboardService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            UserDTO user = UserDTO.builder()
                    .language("ua")
                    .chatId(update.getMessage().getChatId())
                    .userName(update.getMessage().getFrom().getUserName())
                    .build();
            userService.saveUser(user);
            handleCommand(update);
        } else if (update.hasCallbackQuery()) {
            UserDTO user = UserDTO.builder()
                    .language("ua")
                    .chatId(update.getCallbackQuery().getFrom().getId())
                    .userName(update.getCallbackQuery().getFrom().getUserName())
                    .build();
            userService.saveUser(user);
            handleCallback(update);
        }
    }

    private void handleCallback(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();
        String userLanguge = userService.getUserLanguage(Long.parseLong(chatId));
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();


        switch (callbackData) {
            case "price":
                editMessage(chatId, translate("price.message", userLanguge), keyboardService.getPriceKeyboard(userLanguge), messageId);
                break;
            case "info":
                editMessage(chatId, translate("info.message", userLanguge), keyboardService.getInfoKeyboard(userLanguge), messageId);
                break;
            case "language":
                editMessage(chatId, translate("language.message", userLanguge), keyboardService.getLanguageKeyboard(userLanguge), messageId);
                break;
            case "back_main":  // ⬅️ Обработка кнопки "Назад"
                editMessage(chatId, translate("start.message", userLanguge), keyboardService.getMainMenuKeyboard(userLanguge), messageId);
                break;
            case "lang_en":
                userService.saveLanguage(Long.parseLong(chatId), "en");
                editMessage(chatId, "✅ Language changed to English.", keyboardService.getPriceKeyboard(userLanguge), messageId);
                break;
            case "lang_ua":
                userService.saveLanguage(Long.parseLong(chatId), "ua");
                editMessage(chatId, "✅ Мову змінено на Українську.", keyboardService.getPriceKeyboard(userLanguge), messageId);
                break;
        }
    }

    private void handleCommand(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        String userLanguge = userService.getUserLanguage(Long.parseLong(chatId));

        switch (text) {
            case "/start":
                sendMessage(chatId, translate("start.message", userLanguge), keyboardService.getMainMenuKeyboard(userLanguge));
                break;
            case "/price":
                sendMessage(chatId, translate("price.message", userLanguge), keyboardService.getPriceKeyboard(userLanguge));
                break;
            default:
                sendMessage(chatId, priceService.getPrice(text.toUpperCase()));
        }
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    public void sendMessage(String chatId, String text, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (keyboard != null)
            message.setReplyMarkup(keyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Помилка відправки повідомлення: {}", e.getMessage());
        }
    }

    public void sendMessage(String chatId, String text) {
        sendMessage(chatId, text, null); // Вызываем основной метод с `null` в качестве клавиатуры
    }

    public String translate(String key, String language) {
        return translationService.getMessage(key, language);
    }

    public void editMessage(String chatId, String newText, InlineKeyboardMarkup keyboard, Integer messageId) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(keyboard);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка редактирования сообщения: {}", e.getMessage());
        }
    }
}
