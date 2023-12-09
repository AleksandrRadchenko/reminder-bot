package me.alsturm.reminder.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.reminder.config.ReminderProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;

/**
 * Accumulator tries to catch 'forwarded'&'comment' message pair while splitting messages from different users if they
 * send messages simultaneously.
 */
@Slf4j
@Service
public class GroupingUpdatesAccumulator {
    private final TaskScheduler taskScheduler;
    private final ReminderProperties properties;
    private final UpdateProcessor updatesProcessor;

    private final AtomicBoolean accumulatingInProcess = new AtomicBoolean(false);
    private final List<Update> cachedUpdates = new CopyOnWriteArrayList<>();

    public GroupingUpdatesAccumulator(
        TaskScheduler taskScheduler,
        ReminderProperties properties,
        @Qualifier("exceptionHandlingUpdateProcessorDecorator") UpdateProcessor updatesProcessor
    ) {
        this.taskScheduler = taskScheduler;
        this.properties = properties;
        this.updatesProcessor = updatesProcessor;
    }

    public int accumulateUpdates(List<Update> updates) {
        cachedUpdates.addAll(updates);
        if (!accumulatingInProcess.get()) {
            startAccumulating();
        }
        // tech debt: we loose update if app crashes while processing accumulated updates
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void startAccumulating() {
        log.debug("Start accumulating for {}", properties.getAccumulatingDuration());
        Instant targetInstant = taskScheduler.getClock()
            .instant().plus(properties.getAccumulatingDuration());
        taskScheduler.schedule(this::stopAccumulating, targetInstant);
        accumulatingInProcess.compareAndSet(false, true);
    }

    public void stopAccumulating() {
        List<Update> updatesCopy = List.copyOf(cachedUpdates);
        cachedUpdates.clear();
        accumulatingInProcess.compareAndSet(true, false);
        groupBySender(updatesCopy).values().forEach(updatesProcessor::process);
        log.debug("Stop accumulating");
    }

    private Map<User, List<Update>> groupBySender(List<Update> updates) {
        User userForServiceUpdates = new User(0L);
        Function<Update, User> userClassifier = update -> update.message() == null
            ? userForServiceUpdates
            : update.message().from();
        return updates.stream().collect(groupingBy(userClassifier));
    }
}
