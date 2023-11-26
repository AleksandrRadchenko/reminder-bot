package me.alsturm.reminder.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderCommandTest {

    @Test
    void whenUserSendsCyrillic_CommandIsTimer() {
        ReminderCommand expected = ReminderCommand.TIMER;

        ReminderCommand actual = ReminderCommand.from("/е");

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void whenUserSendsLatin_CommandIsTimer() {
        ReminderCommand expected = ReminderCommand.TIMER;

        ReminderCommand actual = ReminderCommand.from("/t");

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void whenNoSlash_CommandIsTimer() {
        ReminderCommand expected = ReminderCommand.TIMER;

        ReminderCommand actual = ReminderCommand.from("1230 message");

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void whenUnknownCommand_CommandIsUnknown() {
        ReminderCommand expected = ReminderCommand.UNKNOWN;

        ReminderCommand actual = ReminderCommand.from("/1230 message");

        assertThat(actual).isSameAs(expected);
    }
}
