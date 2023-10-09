package me.alsturm.timer.service;

import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.config.TimerProperties;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.entity.UserSettings;
import me.alsturm.timer.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
public class UserSettingsService {
    private final TimerProperties properties;
    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(TimerProperties properties, UserSettingsRepository userSettingsRepository) {
        this.properties = properties;
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettings findByIdOrCreateNew(Long id) {
        Optional<UserSettings> maybeUserSettings = userSettingsRepository.findById(id);
        if (maybeUserSettings.isPresent()) {
            log.debug("Found userSettings by id={}", id);
            return maybeUserSettings.get();
        } else {
            log.debug("Created new userSettings for user id={}", id);
            return UserSettings.builder().telegramUserId(id).build();
        }
    }

    @Transactional
    public void updateDefaultMessage(TelegramUser user, String text) {
        log.debug("Updating default message to {}", text);
        updateSettings(user, userSettings -> userSettings.setMessage(text));
    }

    @Transactional
    public void updateDefaultDelay(TelegramUser user, Duration duration) {
        log.debug("Updating default delay to {}", duration);
        updateSettings(user, userSettings -> userSettings.setDelay(duration));
    }

    @SuppressWarnings("UnusedReturnValue")
    private UserSettings updateSettings(TelegramUser user, Consumer<UserSettings> updateAction) {
        UserSettings userSettings = this.findByIdOrCreateNew(user.getId());
        updateAction.accept(userSettings);
        userSettings = userSettingsRepository.save(userSettings);
        return userSettings;
    }

    public String getDefaultMessage(TelegramUser user) {
        return userSettingsRepository
                .findById(user.getId())
                .map(UserSettings::getMessage)
                .orElse("Пора размяться");
    }

    public Duration getDefaultDelay(TelegramUser user) {
        return userSettingsRepository
                .findById(user.getId())
                .map(UserSettings::getDelay)
                .orElse(properties.getDefaultDelay());
    }
}
