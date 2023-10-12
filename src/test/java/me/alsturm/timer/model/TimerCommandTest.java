package me.alsturm.timer.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimerCommandTest {

    @Test
    void whenUserSendsCyrillic_CommandIsTimer() {
        TimerCommand expected = TimerCommand.TIMER;

        TimerCommand actual = TimerCommand.from("/е");

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void whenUserSendsLatin_CommandIsTimer() {
        TimerCommand expected = TimerCommand.TIMER;

        TimerCommand actual = TimerCommand.from("/t");

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void whenNoSlash_CommandIsTimer() {
        TimerCommand expected = TimerCommand.TIMER;

        TimerCommand actual = TimerCommand.from("1230 message");

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void whenUnknownCommand_CommandIsUnknown() {
        TimerCommand expected = TimerCommand.UNKNOWN;

        TimerCommand actual = TimerCommand.from("/1230 message");

        assertThat(actual).isSameAs(expected);
    }
}
