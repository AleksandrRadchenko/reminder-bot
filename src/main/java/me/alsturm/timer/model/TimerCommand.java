package me.alsturm.timer.model;

import java.util.stream.Stream;

public enum TimerCommand {
    START("start"),
    STOP("stop"),
    HELP("help"),
    TIMER("t"),
    UNKNOWN("Unknown command");

    TimerCommand(String text) {
        this.text = text;
    }

    public final String text;

    public static TimerCommand from(String text) {
        return Stream.of(TimerCommand.values())
                .filter(c -> text.toLowerCase().startsWith("/" + c.text.toLowerCase()))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
