package me.alsturm.timer.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@Slf4j
public class CheckerService {
    private final TelegramBot bot;
    private final UpdateProcessor updateProcessor;

    public CheckerService(TelegramBot bot,
                          UpdateProcessor updateProcessor) {
        this.bot = bot;
        this.updateProcessor = updateProcessor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.setUpdatesListener(this::process);
    }

    private int process(List<Update> updates) {
        log.trace("Received updates: {}", updates);
        updates.stream()
                .filter(userInputOrQueryCallback())
                .forEach(updateProcessor::process);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Need only updates with user input or keyboard callback
     */
    @NotNull
    private static Predicate<Update> userInputOrQueryCallback() {
        return update -> {
            boolean result = update.message() != null || update.callbackQuery() != null;
            if (!result) {
                log.warn("Won't handle update: {}", update);
            }
            return result;
        };
    }
}
