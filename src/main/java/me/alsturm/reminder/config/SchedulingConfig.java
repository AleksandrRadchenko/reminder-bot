package me.alsturm.reminder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Separate config to disable in tests
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
