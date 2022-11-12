package me.alsturm.timer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Separate config to disable in tests
 */
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfig {
}
