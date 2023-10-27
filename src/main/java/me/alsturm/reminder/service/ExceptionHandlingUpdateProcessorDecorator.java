package me.alsturm.reminder.service;

import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExceptionHandlingUpdateProcessorDecorator implements UpdateProcessor {

    private final UpdateProcessor delegate;
    private final ExceptionHandler exceptionHandler;

    public ExceptionHandlingUpdateProcessorDecorator(
        @Qualifier("updateProcessorImpl") UpdateProcessor delegate,
        ExceptionHandler exceptionHandler
    ) {
        this.delegate = delegate;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void process(List<Update> updates) {
        try {
            log.trace("Received updates: {}", updates);
            exceptionHandler.printAndResetPreviousErrorIfExists();
            delegate.process(updates);
        } catch (Exception e) {
            exceptionHandler.handleGlobalException(e);
        }
    }
}
