package me.alsturm.reminder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "reminder")
@Component
@Getter
@Setter
public class ReminderProperties {
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
    /**
     * How long to accumulate updates. Accumulator tries to catch
     * 'forwarded'&'comment' message pair. Duration should be non-zero to catch
     * the pair, but less than a time user is able to send two independent
     * messages consecutively.
     */
    private Duration accumulatingDuration = Duration.ofMillis(400);
}
