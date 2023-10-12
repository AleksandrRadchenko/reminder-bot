package me.alsturm.timer.service;

import me.alsturm.timer.entity.UserSettings;
import me.alsturm.timer.model.DelayedMessage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ExtendWith(MockitoExtension.class)
class ParserTest {
    public static final String DEFAULT_PHRASE = "Time to move";
    @InjectMocks
    Parser parser;

    @ParameterizedTest
    @ArgumentsSource(TimerCommandArgumentProvider.class)
    void parseForDelayedMessage(String payload, Optional<DelayedMessage> parsedCommand) {
        UserSettings mockUserSettings = UserSettings.builder()
            .telegramUserId(1L)
            .message(DEFAULT_PHRASE)
            .delay(Duration.ofMinutes(30))
            .build();

        Optional<DelayedMessage> actualParsedCommand = parser.parseForDelayedMessage(payload, mockUserSettings);

        assertThat(actualParsedCommand).isEqualTo(parsedCommand);
    }

    static class TimerCommandArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Duration d5m = Duration.of(5, MINUTES);
            Duration d30m = Duration.of(30, MINUTES);
            DelayedMessage defaultMessage = new DelayedMessage(d30m, DEFAULT_PHRASE);
            return Stream.of(
                Arguments.of("5 Hello", Optional.of(new DelayedMessage(d5m, "Hello"))),
                Arguments.of("5 Hel lo", Optional.of(new DelayedMessage(d5m, "Hel lo"))),
                Arguments.of("there!", Optional.of(new DelayedMessage(d30m, "there!"))),
                Arguments.of("new Hello", Optional.of(new DelayedMessage(d30m, "new Hello"))),
                Arguments.of("5 Hello\nmy dear", Optional.of(new DelayedMessage(d5m, "Hello\nmy dear"))),
                Arguments.of("5 2", Optional.of(new DelayedMessage(d5m, "2"))),
                Arguments.of("hello 2", Optional.of(new DelayedMessage(d30m, "hello 2"))),
                Arguments.of("", Optional.of(defaultMessage)),
                Arguments.of("   Hello", Optional.of(new DelayedMessage(d30m, "Hello"))),
                Arguments.of("Hello   ", Optional.of(new DelayedMessage(d30m, "Hello"))),
                Arguments.of("5", Optional.of(new DelayedMessage(d5m, DEFAULT_PHRASE)))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DurationStringArgumentProvider.class)
    void parseForDuration(String durationStr, Optional<Duration> expectedDuration) {

        Optional<Duration> actualDuration = parser.parseForDuration(durationStr);

        assertThat(actualDuration).isEqualTo(expectedDuration);
    }

    static class DurationStringArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("42", Optional.of(Duration.ofMinutes(42))),
                    Arguments.of("42m", Optional.of(Duration.ofMinutes(42))),
                    Arguments.of("42h", Optional.of(Duration.ofHours(42))),
                    Arguments.of("42H", Optional.of(Duration.ofHours(42))),
                    Arguments.of("42ч", Optional.of(Duration.ofHours(42))),
                    Arguments.of("42Ч", Optional.of(Duration.ofHours(42))),
                    Arguments.of("42a", Optional.of(Duration.ofMinutes(42)))
            );
        }
    }
}
