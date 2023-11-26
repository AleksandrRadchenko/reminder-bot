package me.alsturm.reminder.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CheckerService {
    private final TelegramBot bot;
    private final UpdateProcessor updateProcessor;
    private final ExceptionHandler exceptionHandler;

    public CheckerService(TelegramBot bot,
                          UpdateProcessor updateProcessor,
                          ExceptionHandler exceptionHandler) {
        this.bot = bot;
        this.updateProcessor = updateProcessor;
        this.exceptionHandler = exceptionHandler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.setUpdatesListener(this::process, exceptionHandler::handleTelegramException);
    }

    private int process(List<Update> updates) {
        try {
            log.trace("Received updates: {}", updates);
            exceptionHandler.printAndResetPreviousErrorIfExists();
            updates.forEach(updateProcessor::process);
        } catch (Exception e) {
            exceptionHandler.handleGlobalException(e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
