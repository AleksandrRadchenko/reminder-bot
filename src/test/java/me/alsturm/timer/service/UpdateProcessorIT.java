package me.alsturm.timer.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.IntegrationTestBase;
import me.alsturm.timer.mapper.TelegramUserConverter;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.mocks.MockUser;
import me.alsturm.timer.repository.TelegramUserRepository;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
class UpdateProcessorIT extends IntegrationTestBase {
    @Autowired
    UpdateProcessor updateProcessor;
    @Autowired
    TelegramUserRepository telegramUserRepository;
    @Autowired
    TelegramUserConverter telegramUserConverter;

    @Mock
    Update update;
    @Mock
    Message message;
    private final Long mockUserId = 1L;
    User mockUser = new MockUser(mockUserId)
            .setIsBot(true)
            .setFirstName("NotABot")
            .setLastName("Ivanov")
            .setUsername("botter")
            .setLanguageCode("ru")
            .setIsPremium(false)
            .setAddedToAttachmentMenu(true)
            .setCanJoinGroups(true)
            .setCanReadAllGroupMessages(true)
            .setSupportsInlineQueries(true);

    @BeforeEach
    void init() {
        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(mockUser);
    }

    @Test
    void whenUserIssuesStartCommand_SaveUser() {
        when(message.text()).thenReturn("/start");

        updateProcessor.process(update);

        TelegramUser actualUser = telegramUserRepository.findById(mockUserId).get();
        TelegramUser expectedUser = telegramUserConverter.fromUser(mockUser).setActive(true);
        assertUser(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void whenUserIssuesStopCommand_MarkedAsInactive() {
        when(message.text()).thenReturn("/stop");
        telegramUserRepository.save(telegramUserConverter.fromUser(mockUser));

        updateProcessor.process(update);

        TelegramUser actualUser = telegramUserRepository.findById(mockUserId).get();
        TelegramUser expectedUser = telegramUserConverter.fromUser(mockUser).setActive(false);
        assertUser(actualUser).isEqualTo(expectedUser);
    }

    private AbstractAssert<?, ?> assertUser(TelegramUser actualUser) {
        return assertThat(actualUser).usingRecursiveComparison()
//                .ignoringFields("joinDate")
                ;
    }
}
