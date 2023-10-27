package me.alsturm.reminder.service;

import com.pengrad.telegrambot.model.Update;

import java.util.List;

public interface UpdateProcessor {
    void process(List<Update> updates);
}
