package me.alsturm.timer.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.config.TimerProperties;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.mapper.TelegramUserConverter;
import me.alsturm.timer.model.DelayedMessage;
import me.alsturm.timer.model.SettingsCommand;
import me.alsturm.timer.model.TimerCommand;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.alsturm.timer.model.SettingsCommand.DEFAULT_DELAY_REQUEST;

@SuppressWarnings("unused")
@Service
@Slf4j
public class UpdateProcessor {
    private final TimerProperties properties;
    private final Notifier notifier;
    private final TelegramUserService telegramUserService;

    private final ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
            JsonAutoDetect.Visibility.ANY);

    public UpdateProcessor(TimerProperties properties,
                           Notifier notifier,
                           TelegramUserService telegramUserService) {
        this.properties = properties;
        this.notifier = notifier;
        this.telegramUserService = telegramUserService;
    }

    public void process(Update update) {
        if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            SettingsCommand command = SettingsCommand.from(callbackQuery.data());
            TelegramUser user = TelegramUserConverter.fromUser(callbackQuery.from());
            switch (command) {
                case DEFAULT_DELAY -> setDefaultDelay(user);
                case DEFAULT_MESSAGE -> setDefaultMessage(user);
                default -> notifier.notifyUnknownCommand(user, "");
            }
        } else if (update.message() != null && update.message().replyToMessage() != null) {
            TelegramUser user = TelegramUserConverter.fromUser(update.message().from());
            String text = update.message().text();
            String replyToMessageText = update.message().replyToMessage().text();
            SettingsCommand command = SettingsCommand.from(replyToMessageText);
            switch (command) {
                case DEFAULT_DELAY_REQUEST -> updateDefaultDelay(user, text);
                case DEFAULT_MESSAGE_REQUEST -> updateDefaultMessage(user, text);
                default -> postpone(user, text);
            }
        } else if (update.message() != null) {
            Message message = update.message();
            TelegramUser user = TelegramUserConverter.fromUser(message.from());
            String text = message.text();
            log.info("User {}, message: \"{}\"", user.toShortString(), text);
            TimerCommand command = TimerCommand.from(text);
            switch (command) {
                case START -> start(user);
                case STOP -> stop(user);
                case HELP -> help(user);
                case TIMER -> setTimer(user, text);
                case SET -> settings(user);
                default -> notifier.notifyUnknownCommand(user, text);
            }
        } else {
            log.warn("Won't handle update: {}", update);
        }
    }

    private void updateDefaultDelay(TelegramUser user, String text) {

    }

    private void setDefaultDelay(TelegramUser user) {
        notifier.queryForDefaultDelay(user);
    }

    private void setDefaultMessage(TelegramUser user) {
        notifier.queryForDefaultMessage(user);
    }

    private void start(TelegramUser user) {
        user = telegramUserService.activateUser(user);
        notifier.notifyStart(user);
    }

    private void stop(TelegramUser user) {
        telegramUserService.deactivateUser(user);
    }

    private void help(TelegramUser user) {
        notifier.notifyHelp(user);
    }

    /**
     * Postpone for n minutes 'replyToMessage'
     */
    private void postpone(TelegramUser user, String text) {
        Optional<Duration> mayBeDuration = parseForDuration(text);
        if (mayBeDuration.isPresent()) {
            DelayedMessage delayedMessage = new DelayedMessage(mayBeDuration.get(), message.replyToMessage().text());
            notifier.notifyWithDelay(user, delayedMessage);
        } else {
            notifier.notifyUnknownCommand(user, text);
        }
    }

    /**
     * Expecting '/timer' or '/timer n'
     */
    private void setTimer(TelegramUser user, String text) {
        Optional<DelayedMessage> mayBeDelayedMessage = parseForDelayedMessage(text);
        if (mayBeDelayedMessage.isPresent()) {
            notifier.notifyWithDelay(user, mayBeDelayedMessage.get());
        } else {
            notifier.notifyUnknownCommand(user, text);
        }
    }

    private void settings(TelegramUser user) {
        SendResponse sendResponse = notifier.queryForSettings(user);
        sendResponse.message();
    }

    /**
     * Example: 80 -> 80m, 4h -> 240m
     */
    Optional<Duration> parseForDuration(String text) {
        Pattern pattern = Pattern.compile("(\\d+)([HhЧч]?)");
        Matcher matcher = pattern.matcher(text);
        long duration;
        ChronoUnit chronoUnit = ChronoUnit.MINUTES;
        if (matcher.find()) {
            String durationString = matcher.group(1);
            String unit = matcher.group(2);
            duration = Long.parseLong(durationString); // shouldn't throw thanks to regex
            if (unit != null && !unit.isBlank()) {
                chronoUnit = ChronoUnit.HOURS;
            }
            return Optional.of(Duration.of(duration, chronoUnit));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Example: /timer 4 Wake up -> TimerCommand(4, "Wake up")
     */
    Optional<DelayedMessage> parseForDelayedMessage(String text) {
        Pattern pattern = Pattern.compile("/([a-zA-Zа-яА-Я]+)(?:[\\t ]+(\\d*))?(?:[\\t ]+([\\s\\S]+))?$");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            Duration delay = matcher.group(2) != null
                    ? Duration.ofMinutes(Long.parseLong(matcher.group(2)))
                    : properties.getDefaultDelay();
            String note = matcher.group(3) != null
                    ? matcher.group(3)
                    : "Пора размяться";
            return Optional.of(new DelayedMessage(delay, note));
        } else {
            return Optional.empty();
        }
    }

    private static boolean isPositive(Number... numbers) {
        for (Number id : numbers) {
            if (id == null || id.longValue() <= 0) return false;
        }
        return true;
    }

    private void logSafely(Update update) {
        try {
            log.debug("Update: {}", objectMapper.writeValueAsString(update));
        } catch (JsonProcessingException e) {
            log.error("Swallowing exception from logger:", e);
        }
    }
}
