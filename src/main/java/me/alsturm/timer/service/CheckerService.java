package me.alsturm.timer.service;

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
    private final TelegramExceptionHandler telegramExceptionHandler;

    public CheckerService(TelegramBot bot,
                          UpdateProcessor updateProcessor,
                          TelegramExceptionHandler telegramExceptionHandler) {
        this.bot = bot;
        this.updateProcessor = updateProcessor;
        this.telegramExceptionHandler = telegramExceptionHandler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.setUpdatesListener(this::process, telegramExceptionHandler::handle);
    }

    private int process(List<Update> updates) {
        log.trace("Received updates: {}", updates);
        telegramExceptionHandler.printAndResetPreviousErrorIfExists();
        updates.forEach(updateProcessor::process);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
