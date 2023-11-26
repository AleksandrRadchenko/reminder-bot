package me.alsturm.reminder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class TestConfig {
    @Bean
    @Primary
    public Clock fixedClock() {
        Instant fixedInstant = Instant.parse("2023-10-08T12:57:37.1Z");
        return Clock.fixed(fixedInstant, ZoneId.systemDefault());
    }
}
