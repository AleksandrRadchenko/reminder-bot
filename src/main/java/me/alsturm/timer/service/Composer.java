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
                + "`/" + TimerCommand.TIMER.text + " n` - установить таймер на `n` минут," + newline
                + "`/" + TimerCommand.TIMER.text + "` - на " + properties.getDefaultDelay() + " минут," + newline
                + "`/" + TimerCommand.TIMER.text + " n Coffee done!` - прислать напоминание `Coffee done!`" +
                " через `n` минут," + newline
                + "Можно отложить напоминание на `n` минут, ответив на него числом `n`."
                + " Можно отложить на несколько часов, используя суффикс `h` или `ч`: `3h` или `3ч`.";
    }

    public String composeStart() {
        return "Won't forget to remind you. Will remind for you not to forget! See /help for details.";
    }
}
