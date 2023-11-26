package me.alsturm.reminder.model;

import java.util.stream.Stream;

public enum SettingsCommand {
    DEFAULT_DELAY("defaultDelay"),
    DEFAULT_DELAY_REQUEST("Введите задержку по умолчанию"),
    DEFAULT_MESSAGE("defaultMessage"),
    DEFAULT_MESSAGE_REQUEST("Введите фразу для напоминания по умолчанию"),
    UNKNOWN("Unknown command");

    SettingsCommand(String text) {
        this.text = text;
    }

    public final String text;

    public static SettingsCommand from(String text) {
        if (text == null) {
            return UNKNOWN;
        } else {
            return Stream.of(SettingsCommand.values())
                    .filter(c -> text.toLowerCase().startsWith(c.text.toLowerCase()))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
