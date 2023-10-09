package me.alsturm.timer.service;

import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.config.TimerProperties;
import me.alsturm.timer.model.TimerCommand;
import org.springframework.stereotype.Service;

import static me.alsturm.timer.model.TimerCommand.SET;
import static me.alsturm.timer.model.TimerCommand.TIMER;

@Slf4j
@Service
public class Composer {
    private static final String newline = System.lineSeparator();

    private final TimerProperties properties;

    public Composer(TimerProperties properties) {
        this.properties = properties;
    }

    public String composeHelp() {
        return "Примеры команд:" + newline
                + "`/" + TIMER.aliases.get(0) + " n` - установить таймер на `n` минут," + newline
                + "`/" + TIMER.aliases.get(0) + "` - на " + properties.getDefaultDelay().toMinutes() + " минут," + newline
                + "`/" + TIMER.aliases.get(0) + " n Coffee done!` - прислать напоминание `Coffee done!`" +
                " через `n` минут," + newline
                + "Можно отложить напоминание на `n` минут, ответив на него числом `n`."
                + " Можно отложить на несколько часов, используя суффикс `h` или `ч`: `3h` или `3ч`." + newline
                + "Команды можно задавать транслитом, т.е. вместо `t` русской `е`." + newline
                + "`/" + SET.aliases.get(0) + "` - настройки бота: фраза по умолчанию (не более 1000 символов) и" +
                " задержка для команды `/" + TIMER.aliases.get(0) + "` без аргуметов";
    }

    public String composeStart() {
        return "Won't forget to remind you. Will remind for you not to forget! See /help for details.";
    }
}
