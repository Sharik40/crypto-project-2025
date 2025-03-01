package org.spring.cp.cryptoproject2025.services;

import lombok.RequiredArgsConstructor;
import org.spring.cp.cryptoproject2025.entities.Crypto;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyboardService {

    private final TranslationService translationService;

    public String translate(String key, String language) {
        return translationService.getMessage(key, language);
    }

    public InlineKeyboardMarkup getMainMenuKeyboard(String language) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = List.of(
                createButton(translate("price.kb", language), "price"),
                createButton(translate("info.kb", language), "info")
        );

        List<InlineKeyboardButton> row2 = List.of(
                createButton(translate("language.kb", language), "language"),
                createButton(translate("favorite_main.kb", language), "favorite_main")
        );

        keyboard.add(row1);
        keyboard.add(row2);

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getLanguageKeyboard(String language) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = List.of(
                createButton("🇺🇸 English", "lang_en"),
                createButton("🇺🇦 Українська", "lang_ua")
        );

        List<InlineKeyboardButton> row2 = List.of(
                createButton(translate("back.kb", language), "back_main")
        );

        keyboard.add(row1);
        keyboard.add(row2);

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getInfoKeyboard(String language) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = List.of(
                createButton(translate("back.kb", language), "back_main")
        );

        keyboard.add(row1);

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getPriceKeyboard(String language) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = List.of(
                createButton(translate("back.kb", language), "back_main")
        );

        keyboard.add(row1);

        return new InlineKeyboardMarkup(keyboard);
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public InlineKeyboardMarkup getFavoriteKeyboard(String language) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = List.of(
                createButton(translate("create_favorite.kb", language), "create_favorite"),
                createButton(translate("get_favorite.kb", language), "get_favorite")
        );
        List<InlineKeyboardButton> row2 = List.of(
                createButton(translate("back.kb", language), "back_main")
        );

        keyboard.add(row1);
        keyboard.add(row2);

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup createFavorite(String language) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = List.of(
                createButton(translate("back.kb", language), "back_main")
        );

        keyboard.add(row1);

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup getFavorite(String language, List<Crypto> favorites) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Crypto c : favorites) {
            row.add(createButton(c.getSymbol(),"crypto_" + c.getSymbol()));

            if (row.size() == 2) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        List<InlineKeyboardButton> row2 = List.of(
                createButton(translate("back.kb", language), "back_main")
        );
        keyboard.add(row2);

        return new InlineKeyboardMarkup(keyboard);
    }
}
