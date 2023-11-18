package me.alsturm.timer.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.config.TimerProperties;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.entity.UserSettings;
import me.alsturm.timer.model.DelayedMessage;
import me.alsturm.timer.model.TimerCommand;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

import static me.alsturm.timer.model.SettingsCommand.*;

@SuppressWarnings("unused")
@Slf4j
@Service
public class Notifier {
    private final TelegramBot bot;
    private final Composer composer;
    private final TaskScheduler taskScheduler;
    private final TimerProperties timerProperties;

    public Notifier(TelegramBot bot, Composer composer, TaskScheduler taskScheduler, TimerProperties timerProperties) {
        this.bot = bot;
        this.composer = composer;
        this.taskScheduler = taskScheduler;
        this.timerProperties = timerProperties;
    }

    public void notifyWithDelay(TelegramUser user, DelayedMessage delayedMessage) {
        Instant targetInstant = taskScheduler.getClock().instant().plus(delayedMessage.getDelay());
        taskScheduler.schedule(() -> send(user, delayedMessage.getMessage()), targetInstant);
        log.info("Schedule sending for {}", targetInstant.atZone(ZoneId.systemDefault()));
    }

    public void notifyHelp(TelegramUser user) {
        String text = composer.composeHelp();
        send(user, text);
    }

    public void notifyUnknownCommand(TelegramUser user, String text) {
        log.warn("Unknown command: '{}' from user {}", text, user.toShortString());
        send(user, TimerCommand.UNKNOWN.aliases.get(0));
    }

    public void notifyAdmin(String text) {
        log.warn("Notifying admin");
        TelegramUser admin = new TelegramUser().setId(timerProperties.getSupportContactId());
        sendPlainText(admin, text);
    }

    public void notifyStart(TelegramUser user) {
        send(user, composer.composeStart());
    }

    @SuppressWarnings("UnusedReturnValue")
    public SendResponse queryForSettings(TelegramUser user, UserSettings userSettings) {
        var replyKeyboardMarkup = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Задать сообщение").callbackData(DEFAULT_MESSAGE.text),
                new InlineKeyboardButton("Задать задержку").callbackData(DEFAULT_DELAY.text)
        );
        SendMessage sendMessageRequest =
            new SendMessage(user.getId(), composer.composeSettings(userSettings))
                .parseMode(ParseMode.Markdown)
                .replyMarkup(replyKeyboardMarkup);

        return bot.execute(sendMessageRequest);
    }

    public void queryForDefaultMessage(TelegramUser user) {
        SendMessage sendMessageRequest = new SendMessage(user.getId(), DEFAULT_MESSAGE_REQUEST.text)
                        .replyMarkup(new ForceReply(true));
        bot.execute(sendMessageRequest);
    }

    public void queryForDefaultDelay(TelegramUser user) {
        SendMessage sendMessageRequest = new SendMessage(user.getId(), DEFAULT_DELAY_REQUEST.text)
                        .replyMarkup(new ForceReply(true));
        bot.execute(sendMessageRequest);
    }

    private void send(TelegramUser user, String text) {
        sendInternal(user, text, ParseMode.Markdown);
    }

    private void sendPlainText(TelegramUser user, String text) {
        sendInternal(user, text, null);
    }

    private void sendInternal(TelegramUser user, String text, ParseMode parseMode) {
        SendMessage sendMessageRequest = new SendMessage(user.getId(), text);
        if (parseMode != null) {
            sendMessageRequest.parseMode(parseMode);
        }
        SendResponse response = bot.execute(sendMessageRequest);
        log.info("Message sent. UserId={}", user.getId());
        log.trace("Response: {}", response);
    }
}
