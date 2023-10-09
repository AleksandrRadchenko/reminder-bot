package me.alsturm.timer.service;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class TimeProvider {

    private final Clock clock;

    public TimeProvider(Clock clock) {
        this.clock = clock;
    }

    public Instant now() {
        return Instant.now(clock);
    }
}
