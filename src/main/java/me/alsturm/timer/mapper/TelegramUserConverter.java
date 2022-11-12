package me.alsturm.timer.mapper;

import com.pengrad.telegrambot.model.User;
import me.alsturm.timer.entity.TelegramUser;

import java.time.Instant;

public final class TelegramUserConverter {
    private TelegramUserConverter() {
    }

    public static TelegramUser fromUser(User user) {
        return TelegramUser.builder()
            .id(user.id())
            .isBot(user.isBot())
            .firstName(user.firstName())
            .lastName(user.lastName())
            .username(user.username())
            .languageCode(user.languageCode())
            .joinDate(Instant.now())
            .isActive(true)
            .build();
    }
}
