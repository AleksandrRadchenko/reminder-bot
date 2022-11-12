package me.alsturm.timer.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.model.DelayedMessage;
import me.alsturm.timer.model.TimerCommand;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@Service
public class Notifier {
    private final TelegramBot bot;
    private final Composer composer;
    private final TaskScheduler taskScheduler;

    public Notifier(TelegramBot bot, Composer composer, TaskScheduler taskScheduler) {
        this.bot = bot;
        this.composer = composer;
        this.taskScheduler = taskScheduler;
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
        send(user, TimerCommand.UNKNOWN.text);
    }

    public void notifyError(TelegramUser user, String details) {
        String text = "Произошла техническая ошибка. " + details;
        send(user, text);
    }

    public void notifyStart(TelegramUser user) {
        send(user, composer.composeStart());
    }

    private void send(TelegramUser user, String text) {
        final SendMessage sendMessageRequest = new SendMessage(user.getId(), text).parseMode(ParseMode.Markdown);
        SendResponse response = bot.execute(sendMessageRequest);
        log.info("Message sent. UserId={}", user.getId());
        log.debug("Response: {}", response);
    }
}
