package me.alsturm.timer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "timer")
@Component
@Getter
@Setter
public class TimerProperties {
    /**
     * Set 'true' to log http traffic.
     */
    private boolean debug;
    /**
     * Token to identify the bot. Get it from @BotFather bot.
     */
    private String telegramBotToken;
    /**
     * Default delay for /timer command without arguments. In minutes.
     */
    private Duration defaultDelay = Duration.of(30, ChronoUnit.MINUTES);
    /**
     * Telegram id to send debug info to. Typically, admin of current bot installation.
     */
    private Long supportContactId;
}
