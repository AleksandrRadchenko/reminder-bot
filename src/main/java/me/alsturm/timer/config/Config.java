package me.alsturm.timer.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Config {
    @Bean
    TelegramBot telegramBot(TimerProperties properties) {
        log.info("Initializing bot...");
        TelegramBot.Builder builder = new TelegramBot.Builder(properties.getTelegramBotToken());
        if (properties.isDebug()) {
            builder.debug();
        }
        return builder.build();
    }
}
