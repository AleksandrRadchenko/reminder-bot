package me.alsturm.timer.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
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

import static me.alsturm.timer.model.SettingsCommand.DEFAULT_DELAY;
import static me.alsturm.timer.model.SettingsCommand.DEFAULT_DELAY_REQUEST;
import static me.alsturm.timer.model.SettingsCommand.DEFAULT_MESSAGE;
import static me.alsturm.timer.model.SettingsCommand.DEFAULT_MESSAGE_REQUEST;

@SuppressWarnings("unused")
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
        send(user, TimerCommand.UNKNOWN.aliases.get(0));
    }

    public void notifyError(TelegramUser user, String details) {
        String text = "Произошла техническая ошибка. " + details;
        send(user, text);
    }

    public void notifyStart(TelegramUser user) {
        send(user, composer.composeStart());
    }

    public SendResponse queryForSettings(TelegramUser user) {
        var replyKeyboardMarkup = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Фраза по умолчанию").callbackData(DEFAULT_MESSAGE.text),
                new InlineKeyboardButton("Задержка по умолчанию").callbackData(DEFAULT_DELAY.text)
        );
        SendMessage sendMessageRequest = new SendMessage(user.getId(), "Настройки").replyMarkup(replyKeyboardMarkup);

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
        final SendMessage sendMessageRequest = new SendMessage(user.getId(), text).parseMode(ParseMode.Markdown);
        SendResponse response = bot.execute(sendMessageRequest);
        log.info("Message sent. UserId={}", user.getId());
        log.debug("Response: {}", response);
    }
}
