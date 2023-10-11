package me.alsturm.timer.service;

import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.entity.UserSettings;
import me.alsturm.timer.model.DelayedMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class Parser {

    public static final Pattern PATTERN_DURATION = Pattern.compile("(\\d+)([HЧ]?)",
        Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE);
    public static final Pattern PATTERN_DELAYED_MESSAGE = Pattern.compile("/([a-zA-Zа-яА-Я]+)" // 1 group: command
        + "(?:[\\t ]+" + PATTERN_DURATION + ")?" // 2,3 group: duration
        + "(?:[\\t ]+([\\s\\S]+))?"  // 4 group: message
        + "$",
        Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE); // EOL

    /**
     * Example: 80 -> 80m, 4h -> 240m
     */
    public Optional<Duration> parseForDuration(String text) {
        Matcher matcher = PATTERN_DURATION.matcher(text);
        if (matcher.find()) {
            String durationString = matcher.group(1);
            String unit = matcher.group(2);
            long duration = Long.parseLong(durationString); // shouldn't throw thanks to regex
            ChronoUnit chronoUnit = StringUtils.hasText(unit)
                ? ChronoUnit.HOURS
                : ChronoUnit.MINUTES;
            Duration result = Duration.of(duration, chronoUnit);
            log.debug("Parse '{}' for duration '{}'", text, result);
            return Optional.of(result);
        } else {
            log.debug("Failed to parse duration from '{}'", text);
            return Optional.empty();
        }
    }

    /**
     * Example: /timer 4 Wake up -> TimerCommand(4, "Wake up")
     */
    public Optional<DelayedMessage> parseForDelayedMessage(String text, UserSettings userSettings) {
        Matcher matcher = PATTERN_DELAYED_MESSAGE.matcher(text);
        if (matcher.find()) {
            Duration delay = Optional.ofNullable(matcher.group(2))
                .flatMap(this::parseForDuration)
                .orElse(userSettings.getDelay());
            String note = Optional.ofNullable(matcher.group(4))
                .orElse(userSettings.getMessage());
            return Optional.of(new DelayedMessage(delay, note));
        } else {
            return Optional.empty();
        }
    }
}
