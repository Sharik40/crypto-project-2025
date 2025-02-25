package org.spring.cp.cryptoproject2025.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TranslationService {
    private final Map<String, Map<String, String>> translations = new ConcurrentHashMap<>();

    public TranslationService() {
        loadTranslations();
    }

    private void loadTranslations() {
        try {
            String jsonContent = new String(Files.readAllBytes(
                    Paths.get(new ClassPathResource("translations.json").getURI())));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonContent);

            rootNode.fields().forEachRemaining(entry -> {
                String lang = entry.getKey();
                Map<String, String> messages = mapper.convertValue(entry.getValue(), Map.class);
                translations.put(lang, messages);
            });

            log.info("Translations loaded successfully.");
        } catch (IOException e) {
            log.error("Failed to load translations: {}", e.getMessage());
        }
    }

    public String getMessage(String key, String language) {
        return translations.getOrDefault(language, translations.get("en")).getOrDefault(key, key);
    }
}
