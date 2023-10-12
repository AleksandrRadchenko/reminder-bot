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

import static me.alsturm.timer.model.TimerCommand.COMMAND_PREFIX;

@Service
@Slf4j
public class Parser {
    public static final Pattern PATTERN_COMMAND = Pattern.compile(COMMAND_PREFIX + "[a-zA-Zа-яА-Я]+\\s*");
    public static final Pattern PATTERN_PAYLOAD = Pattern.compile(PATTERN_COMMAND + "(.*)"); // 1 group
    public static final Pattern PATTERN_DURATION = Pattern.compile("(\\d+)([HhЧч]?)\\s*"); //1,2 group
    public static final Pattern PATTERN_DELAYED_MESSAGE = Pattern.compile(
            "(?:" + PATTERN_DURATION + ")?" // 1,2 group
            + "([\\S\\s]*)"  // 3 group: message
            + "$"); // EOL

    /**
     * Examples:
     * <ul>
     * <li> /t 12 Message -> 12 Message</li>
     * <li>Go 12 -> Go 12</li>
     * </ul>
     *
     * @param text message to parse
     * @return payload of the message, without command
     */
    public String parseForPayload(String text) {
        String payload = "";
        if (text.startsWith(COMMAND_PREFIX)) {
            Matcher matcher = PATTERN_PAYLOAD.matcher(text);
            if (matcher.find()) {
                payload = matcher.group(1);
            }
        } else {
            payload = text;
        }
        return payload;
    }

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
     * Example: 4 Wake up -> TimerCommand(4, "Wake up")
     */
    public Optional<DelayedMessage> parseForDelayedMessage(String payload, UserSettings userSettings) {
        Matcher matcher = PATTERN_DELAYED_MESSAGE.matcher(payload);
        if (matcher.find()) {
            Duration delay = Optional.ofNullable(matcher.group(1))
                .flatMap(this::parseForDuration)
                .orElse(userSettings.getDelay());
            String note = Optional.ofNullable(matcher.group(3))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .orElse(userSettings.getMessage());
            return Optional.of(new DelayedMessage(delay, note));
        } else {
            return Optional.empty();
        }
    }
}
