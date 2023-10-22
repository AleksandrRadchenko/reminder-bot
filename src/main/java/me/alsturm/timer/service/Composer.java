package me.alsturm.timer.service;

import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.config.TimerProperties;
import org.springframework.stereotype.Service;

import static me.alsturm.timer.model.TimerCommand.SET;

@Slf4j
@Service
public class Composer {
    private static final String newline = System.lineSeparator();

    private final TimerProperties properties;

    public Composer(TimerProperties properties) {
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
}
