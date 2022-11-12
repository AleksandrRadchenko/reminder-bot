package me.alsturm.timer.service;

import me.alsturm.timer.config.TimerProperties;
import me.alsturm.timer.model.DelayedMessage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ExtendWith(MockitoExtension.class)
class UpdateProcessorTest {
    @Mock
    TimerProperties timerProperties;
    @InjectMocks
    UpdateProcessor updateProcessor;

    @ParameterizedTest
    @ArgumentsSource(TimerCommandArgumentProvider.class)
    void parseTimerCommand(String command, Optional<DelayedMessage> parsedCommand) {
        lenient().when(timerProperties.getDefaultDelay()).thenReturn(Duration.ofMinutes(30));

        Optional<DelayedMessage> actualParsedCommand = updateProcessor.parseForDelayedMessage(command);

        assertThat(actualParsedCommand).isEqualTo(parsedCommand);
    }

    static class TimerCommandArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Duration d5m = Duration.of(5, MINUTES);
            Duration d30m = Duration.of(30, MINUTES);
            return Stream.of(
                    Arguments.of("/timer 5 Hello", Optional.of(new DelayedMessage(d5m, "Hello"))),
                    Arguments.of("/timer 5 Hel lo", Optional.of(new DelayedMessage(d5m, "Hel lo"))),
                    Arguments.of("/t5Hello there!", Optional.empty()),
                    Arguments.of("/timer new Hello", Optional.of(new DelayedMessage(d30m, "new Hello"))),
                    Arguments.of("/timer 5 Hello\nmy dear", Optional.of(new DelayedMessage(d5m, "Hello\nmy dear"))),
                    Arguments.of("/timer 5 2", Optional.of(new DelayedMessage(d5m, "2"))),
                    Arguments.of("/timer hello 2", Optional.of(new DelayedMessage(d30m, "hello 2"))),
                    Arguments.of("/timer", Optional.of(new DelayedMessage(d30m, "Пора размяться"))),
                    Arguments.of("/timer Hello", Optional.of(new DelayedMessage(d30m, "Hello"))),
                    Arguments.of("/timer 5", Optional.of(new DelayedMessage(d5m, "Пора размяться"))),
                    Arguments.of("/t 5", Optional.of(new DelayedMessage(d5m, "Пора размяться")))
            );
        }
    }
}
