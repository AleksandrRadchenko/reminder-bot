package me.alsturm.reminder.model;

import lombok.Value;

import java.time.Duration;

@Value
public class DelayedMessage {
    Duration delay;
    String message;
}
