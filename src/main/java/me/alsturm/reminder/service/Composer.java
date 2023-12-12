package me.alsturm.reminder.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.reminder.config.ReminderProperties;
import me.alsturm.reminder.entity.UserSettings;
import me.alsturm.reminder.exception.JsonException;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static me.alsturm.reminder.model.ReminderCommand.SET;

@Slf4j
@Service
public class Composer {
    private static final String newline = System.lineSeparator();

    private final ReminderProperties properties;

    public Composer(ReminderProperties properties) {
        this.properties = properties;
    }

    public String composeHelp() {
        return "*Установить* напоминание:" + newline
                + " • `n` - установить таймер на `n` минут. Бот пришлет стандартное сообщение через `n` минут." + newline
                + " • `любая фраза` - бот пришлет сообщение `любая фраза` через " + properties.getDefaultDelay().toMinutes() + " минут," + newline
                + " • `n Coffee done!` - бот пришлет сообщение `Coffee done!` через `n` минут," + newline
                + "*Отложить* напоминание на `n` минут, ответив на него числом `n`." + newline
                + "Форматы задержки: " + newline
                + " • `3h20m`, `3ч20м`, `3H20M`, `3h20` - 3 часа 20 минут." + newline
                + " • `80`, `80m`, `80м` - 80 минут." + newline
                + " • `3h`, `3ч` - 3 часа." + newline
                + "*Настройки* бота: `/" + SET.aliases.get(0) + "`. Можно задать фразу по умолчанию (не более 1000 символов) и" +
                " задержку по умолчанию";
    }

    public String composeStart() {
        return "Не забуду напомнить! Напомню, чтобы не забыть! Помощь: /help";
    }

    public String composeSettings(UserSettings userSettings) {
        Duration delay = userSettings.getDelay();
        return "Настройки:" + newline
            + " • сообщение по умолчанию: `" + userSettings.getMessage() + "`" + newline
            + " • задержка по умолчанию: `" + formatDuration(delay) + "`";
    }

    private String formatDuration(Duration duration) {
        long m = duration.toMinutes();
        if (m < 60) {
            return String.format("%dм", m);
        } else {
            return String.format("%dч%02dм", m / 60, (m % 60));
        }
    }

    public String toPrettyJson(Object anyObject) {
        ObjectMapper objectMapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(anyObject);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }
    }
}
