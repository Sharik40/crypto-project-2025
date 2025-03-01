package org.spring.cp.cryptoproject2025.bot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.spring.cp.cryptoproject2025.dto.UserDTO;
import org.spring.cp.cryptoproject2025.dto.UserStates;
import org.spring.cp.cryptoproject2025.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class TelegramBot extends TelegramLongPollingBot {

    private String botName;

    private String globalSymbol;

    private final String startText = "Привіт\n\nЦей бот може надати поточну ціну криптовалюти\n\nНадішліть боту `/price` та бажану криптовалютну пару у форматі `BTCUSDT`";

    @Autowired
    private PriceService priceService;

    @Autowired
    private UserService userService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private PriceAlertService priceAlertService;

    private final KeyboardService keyboardService;

    public TelegramBot(String botName, String botToken, KeyboardService keyboardService) {
        super(botToken);
        this.botName = botName;
        this.keyboardService = keyboardService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String message = update.getMessage().getText().trim().toUpperCase();
            UserStates userState = userService.getUserState(Long.parseLong(chatId));


            UserDTO user = UserDTO.builder()
                    .language("ua")
                    .chatId(Long.parseLong(chatId))
                    .userName(update.getMessage().getFrom().getUserName())
                    .userState(UserStates.DEFAULT)
                    .build();
            userService.saveUser(user);

            switch (userState) {
                case WAITING_FOR_PRICE:
                    String[] priceWithSymbol = priceService.getPriceWithSymbol(message.toUpperCase()).split(" ");
                    String language = userService.getUserLanguage(Long.parseLong(chatId));
                    sendMessage(chatId, String.format(translate("price_response.message", language), priceWithSymbol[0], priceWithSymbol[1]), keyboardService.backToMenu(language));
                    break;
                case WAITING_FOR_CRYPTO:
                    if (cryptoService.addCrypto(message, Long.parseLong(chatId))) {
                        userService.setUserState(Long.parseLong(chatId), UserStates.DEFAULT);
                        sendMessage(chatId, "✅ " + message + translate("add_success.message", userService.getUserLanguage(Long.parseLong(chatId))), keyboardService.getFavoriteKeyboard(userService.getUserLanguage(Long.parseLong(chatId))));
                    } else {
                        sendMessage(chatId, translate("add_denied.message", userService.getUserLanguage(Long.parseLong(chatId))), keyboardService.getFavoriteKeyboard(userService.getUserLanguage(Long.parseLong(chatId))));
                    }
                    break;
                case WAITING_FOR_TARGET_PRICE:
                    String symbol = userService.getTargetSymbol(Long.parseLong(chatId));
                    userService.setUserState(Long.parseLong(chatId), UserStates.DEFAULT);
                    try {
                        BigDecimal targetPrice = new BigDecimal(message);
                        priceAlertService.addAlert(Long.parseLong(chatId), symbol, targetPrice);
                        sendMessage(chatId, String.format(translate("add_crypto_success.message", userService.getUserLanguage(Long.parseLong(chatId))), symbol), keyboardService.backToMenu(userService.getUserLanguage(Long.parseLong(chatId))));
                    } catch (NumberFormatException e) {
                        log.error(e.getMessage());
                        userService.setUserState(Long.parseLong(chatId), UserStates.WAITING_FOR_TARGET_PRICE);
                        sendMessage(chatId, translate("enter_number.message", userService.getUserLanguage(Long.parseLong(chatId))), keyboardService.backToMenu(userService.getUserLanguage(Long.parseLong(chatId))));
                    }

                default:
                    handleCommand(update);
            }
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
            case "lang_en":
                userService.saveLanguage(Long.parseLong(chatId), "en");
                editMessage(chatId, "✅ Language changed to English.", keyboardService.getPriceKeyboard(userLanguge), messageId);
                break;
            case "lang_ua":
                userService.saveLanguage(Long.parseLong(chatId), "ua");
                editMessage(chatId, "✅ Мову змінено на Українську.", keyboardService.getPriceKeyboard(userLanguge), messageId);
                break;


            case "price":
                userService.setUserState(Long.parseLong(chatId), UserStates.WAITING_FOR_PRICE);
                editMessage(chatId, translate("price.message", userLanguge), keyboardService.getPriceKeyboard(userLanguge), messageId);
                break;
            case "info":
                editMessage(chatId, translate("info.message", userLanguge), keyboardService.getInfoKeyboard(userLanguge), messageId);
                break;
            case "language":
                editMessage(chatId, translate("language.message", userLanguge), keyboardService.getLanguageKeyboard(userLanguge), messageId);
                break;
            case "back_main":  // ⬅️ Обработка кнопки "Назад"
                userService.setUserState(Long.parseLong(chatId), UserStates.DEFAULT);
                editMessage(chatId, translate("start.message", userLanguge), keyboardService.getMainMenuKeyboard(userLanguge, Long.parseLong(chatId)), messageId);
                break;
            case "favorite_main":
                editMessage(chatId, translate("favorite_main.message", userLanguge), keyboardService.getFavoriteKeyboard(userLanguge), messageId);
                break;
            case "create_favorite":
                userService.setUserState(Long.parseLong(chatId), UserStates.WAITING_FOR_CRYPTO);
                editMessage(chatId, translate("create_favorite.message", userLanguge), keyboardService.createFavorite(userLanguge), messageId);
                break;
            case "get_favorite":
                editMessage(chatId, translate("list_of_favorites.message", userLanguge), keyboardService.getFavorite(userLanguge, cryptoService.getAllCryptos(Long.parseLong(chatId))), messageId);
                break;
            case "set_price_favorite":
                userService.setUserState(Long.parseLong(chatId), UserStates.WAITING_FOR_TARGET_PRICE);
                String price_set = priceService.getPrice(globalSymbol).toString();
                editMessage(chatId, String.format(translate("set_price_favorite.message", userLanguge), globalSymbol, price_set), keyboardService.backToMenu(userLanguge), messageId);
                break;
            case "price_favorite":
                String price = priceService.getPrice(globalSymbol).toString();
                editMessage(chatId, String.format(translate("price_response.message", userLanguge), globalSymbol, price), keyboardService.backToMenu(userLanguge), messageId);
                break;
            default:
                if (callbackData.startsWith("crypto_")) {
                    String symbol = callbackData.substring("crypto_".length());
                    userService.setTargetSymbol(Long.parseLong(chatId), symbol);
                    globalSymbol = symbol;
                    editMessage(chatId, String.format(translate("crypto_of_favorites.message", userLanguge), symbol), keyboardService.getCryptoOfFavorites(userLanguge), messageId);
                }
        }
    }

    private void handleCommand(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        String userLanguge = userService.getUserLanguage(Long.parseLong(chatId));

        switch (text) {
            case "/start":
                userService.setUserState(Long.parseLong(chatId), UserStates.DEFAULT);
                sendMessage(chatId, translate("start.message", userLanguge), keyboardService.getMainMenuKeyboard(userLanguge, Long.parseLong(chatId)));
                break;
            case "/price":
                sendMessage(chatId, translate("price.message", userLanguge), keyboardService.getPriceKeyboard(userLanguge));
                break;
//            default:
//                sendMessage(chatId, priceService.getPrice(text.toUpperCase()));
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
