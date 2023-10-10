package me.alsturm.timer.service;

import com.pengrad.telegrambot.TelegramException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * NOT THREADSAFE!
 */
@Slf4j
@Service
public class TelegramExceptionHandler {

    private Exception previousNetworkError;
    private long networkErrorCount;

    public void printAndResetPreviousErrorIfExists() {
        if (previousNetworkError != null) {
            log.error("{}, total count={}", previousNetworkError.getMessage(), networkErrorCount);
            previousNetworkError = null;
            networkErrorCount = 0;
        }
    }

    public void handle(TelegramException e) {
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
