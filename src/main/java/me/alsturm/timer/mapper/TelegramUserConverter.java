package me.alsturm.timer.mapper;

import com.pengrad.telegrambot.model.User;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.service.TimeProvider;
import org.springframework.stereotype.Component;

@Component
public final class TelegramUserConverter {
    private final TimeProvider timeProvider;

    private TelegramUserConverter(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public TelegramUser fromUser(User user) {
        return TelegramUser.builder()
            .id(user.id())
            .isBot(user.isBot())
            .firstName(user.firstName())
            .lastName(user.lastName())
            .username(user.username())
            .languageCode(user.languageCode())
            .joinDate(timeProvider.now())
            .isActive(true)
            .build();
    }
}
