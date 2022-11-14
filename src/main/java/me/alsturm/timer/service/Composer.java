package me.alsturm.timer.service;

import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.config.TimerProperties;
import me.alsturm.timer.model.TimerCommand;
import org.springframework.stereotype.Service;

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
                + "`/" + TimerCommand.TIMER.aliases.get(0) + " n` - установить таймер на `n` минут," + newline
                + "`/" + TimerCommand.TIMER.aliases.get(0) + "` - на " + properties.getDefaultDelay().toMinutes() + " минут," + newline
                + "`/" + TimerCommand.TIMER.aliases.get(0) + " n Coffee done!` - прислать напоминание `Coffee done!`" +
                " через `n` минут," + newline
                + "Можно отложить напоминание на `n` минут, ответив на него числом `n`."
                + " Можно отложить на несколько часов, используя суффикс `h` или `ч`: `3h` или `3ч`." + newline
                + "Команды можно задавать транслитом, т.е. вместо `t` русской `е`.";
    }

    public String composeStart() {
        return "Won't forget to remind you. Will remind for you not to forget! See /help for details.";
    }
}
