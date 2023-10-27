package me.alsturm.reminder.service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckerService {
    private final TelegramBot bot;
    private final ExceptionHandler exceptionHandler;
    private final GroupingUpdatesAccumulator groupingUpdatesAccumulator;

    public CheckerService(TelegramBot bot,
                          ExceptionHandler exceptionHandler,
                          GroupingUpdatesAccumulator groupingUpdatesAccumulator) {
        this.bot = bot;
        this.exceptionHandler = exceptionHandler;
        this.groupingUpdatesAccumulator = groupingUpdatesAccumulator;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        bot.setUpdatesListener(groupingUpdatesAccumulator::accumulateUpdates, exceptionHandler::handleTelegramException);
    }
}
