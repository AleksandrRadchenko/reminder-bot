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
        updates.forEach(updateProcessor::process);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
