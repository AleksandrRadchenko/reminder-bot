package me.alsturm.reminder.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.reminder.entity.TelegramUser;
import me.alsturm.reminder.entity.UserSettings;
import me.alsturm.reminder.mapper.TelegramUserConverter;
import me.alsturm.reminder.model.DelayedMessage;
import me.alsturm.reminder.model.SettingsCommand;
import me.alsturm.reminder.model.ReminderCommand;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UpdateProcessorImpl implements UpdateProcessor {
    private final TelegramUserConverter telegramUserConverter;
    private final Notifier notifier;
    private final Parser parser;
    private final TelegramUserService telegramUserService;
    private final UserSettingsService userSettingsService;

    private final ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
            JsonAutoDetect.Visibility.ANY);

    public UpdateProcessorImpl(TelegramUserConverter telegramUserConverter,
                               Notifier notifier,
                               Parser parser,
                               TelegramUserService telegramUserService,
                               UserSettingsService userSettingsService) {
        this.telegramUserConverter = telegramUserConverter;
        this.notifier = notifier;
        this.parser = parser;
        this.telegramUserService = telegramUserService;
        this.userSettingsService = userSettingsService;
    }

    public void process(@NotNull List<Update> updatesFromOneSender) {
        log.debug("Received {} updates", updatesFromOneSender.size());
        if (CollectionUtils.isEmpty(updatesFromOneSender)) {
            notifier.notifyAdmin("Received empty update list");
        } else {
            if (updatesFromOneSender.get(0).message() == null) {
                updatesFromOneSender.forEach(update -> notifier.notifyAdmin("Won't handle tech update: " + update));
            } else {
                processInternal(updatesFromOneSender);
            }
        }
    }

    //TODO: replace with strategies: ProcessKeyboardButtonPressStrategy, ProcessReplyStrategy, ... see https://ru.yougile.com/team/406a0929992c/#REM-25
    public void processInternal(List<Update> updates) {
        if (updates.size() == 1) {
            Update update = updates.get(0);
            if (update.callbackQuery() != null) {
                processKeyboardButtonPress(update);
            } else if (update.message() != null && update.message().replyToMessage() != null) {
                processReply(update);
            } else if (update.message() != null && update.message().forwardFrom() != null) {
                processForwardedMessage(update);
            } else if (update.message() != null) {
                processCommand(update);
            } else {
                notifier.notifyAdmin("Won't handle update: " + update);
            }
        } else if (updates.size() == 2) {
            processForwardedMessage(updates);
        } else {
            notifier.notifyAdmin("Unsupported updates size: " + updates.size() + ". Updates:");
            updates.forEach(update -> notifier.notifyAdmin(update.toString()));
        }
    }

    private void processKeyboardButtonPress(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        SettingsCommand command = SettingsCommand.from(callbackQuery.data());
        TelegramUser user = telegramUserConverter.fromUser(callbackQuery.from());
        switch (command) {
            case DEFAULT_DELAY -> notifier.queryForDefaultDelay(user);
            case DEFAULT_MESSAGE -> notifier.queryForDefaultMessage(user);
            default -> notifier.notifyUnknownCommand(user, "");
        }
    }

    private void processReply(Update update) {
        TelegramUser user = telegramUserConverter.fromUser(update.message().from());
        String text = update.message().text();
        String replyToMessageText = update.message().replyToMessage().text();
        SettingsCommand command = SettingsCommand.from(replyToMessageText);
        switch (command) {
            case DEFAULT_DELAY_REQUEST -> parser.parseForDuration(text)
                    .ifPresent(duration -> userSettingsService.updateDefaultDelay(user, duration));
            case DEFAULT_MESSAGE_REQUEST -> userSettingsService.updateDefaultMessage(user, text);
            default -> postpone(user, update.message());
        }
    }

    private void processCommand(Update update) {
        Message message = update.message();
        TelegramUser user = telegramUserConverter.fromUser(message.from());
        String text = message.text();
        log.info("User {}, message: \"{}\"", user.toShortString(), text);
        ReminderCommand command = ReminderCommand.from(text);
        switch (command) {
            case START -> start(user);
            case STOP -> stop(user);
            case HELP -> help(user);
            case TIMER -> setReminder(user, text);
            case SET -> settings(user);
            case UNKNOWN -> {
                notifier.notifyUnknownCommand(user, text);
                notifier.notifyAdmin("Unknown command. Update: " + update);
            }
        }
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
    private void postpone(TelegramUser user, Message message) {
        Optional<Duration> mayBeDuration = parser.parseForDuration(message.text());
        if (mayBeDuration.isPresent()) {
            DelayedMessage delayedMessage = new DelayedMessage(mayBeDuration.get(), message.replyToMessage().text());
            notifier.notifyWithDelay(user, delayedMessage);
        } else {
            notifier.notifyUnknownCommand(user, message.text());
        }
    }

    /**
     * Postpone forwarded message, if no delay specified
     */
    private void processForwardedMessage(Update update) {
        TelegramUser user = telegramUserConverter.fromUser(update.message().from());
        UserSettings userSettings = userSettingsService.findByIdOrDefault(user.getId());
        DelayedMessage delayedMessage = new DelayedMessage(userSettings.getDelay(), update.message().text());
        notifier.notifyWithDelay(user, delayedMessage);
    }

    /**
     * Postpone for n minutes forwarded message
     */
    private void processForwardedMessage(List<Update> updates) {
        Update delay = updates.get(0);
        Update forwarded = updates.get(1);
        TelegramUser user = telegramUserConverter.fromUser(forwarded.message().from());
        Optional<Duration> mayBeDuration = parser.parseForDuration(delay.message().text());
        if (mayBeDuration.isPresent()) {
            DelayedMessage delayedMessage = new DelayedMessage(mayBeDuration.get(), forwarded.message().text());
            notifier.notifyWithDelay(user, delayedMessage);
        } else {
            notifier.notifyUnknownCommand(user, delay.message().text());
        }
    }

    /**
     * Expecting '/timer' or '/timer n'
     */
    private void setReminder(TelegramUser user, String text) {
        UserSettings userSettings = userSettingsService.findByIdOrDefault(user.getId());
        String payload = parser.parseForPayload(text);
        Optional<DelayedMessage> mayBeDelayedMessage = parser.parseForDelayedMessage(payload, userSettings);
        if (mayBeDelayedMessage.isPresent()) {
            notifier.notifyWithDelay(user, mayBeDelayedMessage.get());
        } else {
            notifier.notifyUnknownCommand(user, text);
        }
    }

    private void settings(TelegramUser user) {
        UserSettings userSettings = userSettingsService.findByIdOrDefault(user.getId());
        notifier.queryForSettings(user, userSettings);
    }

    @SuppressWarnings("unused") // for future use
    private void logSafely(Update update) {
        try {
            log.debug("Update: {}", objectMapper.writeValueAsString(update));
        } catch (JsonProcessingException e) {
            log.error("Swallowing exception from logger:", e);
        }
    }
}
