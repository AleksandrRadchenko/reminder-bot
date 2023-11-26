package me.alsturm.reminder.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Slf4j
@Configuration
public class Config {
    @Bean
    TelegramBot telegramBot(ReminderProperties properties) {
        log.info("Initializing bot...");
        TelegramBot.Builder builder = new TelegramBot.Builder(properties.getTelegramBotToken());
        if (properties.isDebug()) {
            builder.debug();
        }
        return builder.build();
    }

    @Bean
    public Clock systemUtcClock() {
        return Clock.systemUTC();
    }
}
