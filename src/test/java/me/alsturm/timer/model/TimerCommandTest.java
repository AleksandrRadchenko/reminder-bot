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
}
