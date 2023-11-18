package me.alsturm.timer.service;

import com.pengrad.telegrambot.TelegramException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * NOT THREADSAFE!
 */
@Slf4j
@Service
public class ExceptionHandler {

    private final Notifier notifier;

    private Exception previousNetworkError;
    private long networkErrorCount;

    public ExceptionHandler(Notifier notifier) {
        this.notifier = notifier;
    }

    public void handleGlobalException(@NotNull Exception e) {
        try {
            log.error(e.getMessage());
            List<StackTraceElement> fiveFirstStackFrames = Arrays.stream(e.getStackTrace()).limit(5).toList();
            notifier.notifyAdmin("Exception. Message: '" + e.getMessage() + "'. Stack trace:\n" + fiveFirstStackFrames);
        } catch (Exception exception) {
            log.error("Exception while handling exception.", exception);
        }
    }

    public void printAndResetPreviousErrorIfExists() {
        if (previousNetworkError != null) {
            log.error("{}, total count={}", previousNetworkError.getMessage(), networkErrorCount);
            previousNetworkError = null;
            networkErrorCount = 0;
        }
    }

    public void handleTelegramException(TelegramException e) {
        if (e.response() != null) {
            // got bad response from telegram
            log.error("Code={}, decr={}", e.response().errorCode(), e.response().description());
        } else {
            // probably network error
            groupSimilarErrors(e);
        }
    }

    private void groupSimilarErrors(TelegramException e) {
        if (errorRepeats(e)) {
            networkErrorCount++;
            if (networkErrorCount % 1000 == 0) {
                log.error("{}, count={}", e.getMessage(), networkErrorCount);
            }
        } else {
            printAndResetPreviousErrorIfExists();
            log.error("{}", e.getMessage());
            previousNetworkError = e;
        }
    }

    private boolean errorRepeats(TelegramException e) {
        return previousNetworkError != null && e.getMessage() != null
            && e.getMessage().equals(previousNetworkError.getMessage());
    }
}
