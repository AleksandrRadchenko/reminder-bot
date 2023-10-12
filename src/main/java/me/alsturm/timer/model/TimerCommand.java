package me.alsturm.timer.model;

import java.util.List;

public enum TimerCommand {
    TIMER("t", "е"), // е - cyrillic
    SET("set", "ыуе"),
    HELP("help"),
    START("start"),
    STOP("stop"),
    UNKNOWN("Unknown command");

    public static final String COMMAND_PREFIX = "/";

    TimerCommand(String... aliases) {
        this.aliases = List.of(aliases);
    }

    public final List<String> aliases;

    public static TimerCommand from(String text) {
        if (text == null) {
            return UNKNOWN;
        }
        if (text.startsWith(COMMAND_PREFIX)) {
            for (TimerCommand command : TimerCommand.values()) {
                if (command.aliasIsBeginningOf(text)) {
                    return command;
                }
            }
            return UNKNOWN;
        } else {
            return TIMER;
        }
    }

    private boolean aliasIsBeginningOf(String text) {
        return this.aliases.stream().anyMatch(alias -> text.toLowerCase().startsWith(COMMAND_PREFIX + alias.toLowerCase()));
    }
}
