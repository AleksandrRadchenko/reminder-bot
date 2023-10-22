package me.alsturm.timer.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.IntegrationTestBase;
import me.alsturm.timer.entity.UserSettings;
import me.alsturm.timer.mapper.TelegramUserConverter;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.mocks.MockUser;
import me.alsturm.timer.model.TimerCommand;
import me.alsturm.timer.repository.TelegramUserRepository;
import me.alsturm.timer.repository.UserSettingsRepository;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static me.alsturm.timer.model.TimerCommand.COMMAND_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
class UpdateProcessorIT extends IntegrationTestBase {
    @Autowired
    UpdateProcessor updateProcessor;
    @Autowired
    TelegramUserRepository telegramUserRepository;
    @Autowired
    UserSettingsRepository userSettingsRepository;
    @Autowired
    TelegramUserConverter telegramUserConverter;

    @Mock
    Update update;
    @Mock
    Message message;
    private final Long stubUserId = 1L;
    User stubUser = new MockUser(stubUserId)
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
    UserSettings stubSettings = new UserSettings(stubUserId, "stubMessage", Duration.ofMinutes(15));
    @Captor
    ArgumentCaptor<SendMessage> sendMessageArgumentCaptor;

    @BeforeEach
    void init() {
        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(stubUser);
    }

    @Test
    void whenUserIssuesStartCommand_SaveUser() {
        when(message.text()).thenReturn(COMMAND_PREFIX + TimerCommand.START.aliases.get(0));

        updateProcessor.process(update);

        TelegramUser actualUser = telegramUserRepository.findById(stubUserId).get();
        TelegramUser expectedUser = telegramUserConverter.fromUser(stubUser).setActive(true);
        assertRecursively(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void whenUserIssuesStopCommand_MarkedAsInactive() {
        when(message.text()).thenReturn(COMMAND_PREFIX + TimerCommand.STOP.aliases.get(0));
        telegramUserRepository.save(telegramUserConverter.fromUser(stubUser));

        updateProcessor.process(update);

        TelegramUser actualUser = telegramUserRepository.findById(stubUserId).get();
        TelegramUser expectedUser = telegramUserConverter.fromUser(stubUser).setActive(false);
        assertRecursively(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void whenUserIssuesSetCommand_UserSettingsDisplayed() {
        when(message.text()).thenReturn(COMMAND_PREFIX + TimerCommand.SET.aliases.get(0));
        userSettingsRepository.save(stubSettings);

        updateProcessor.process(update);

        verify(telegramBot).execute(sendMessageArgumentCaptor.capture());
        SendMessage actualSendMessage = sendMessageArgumentCaptor.getValue();
        assertThat((String) actualSendMessage.getParameters().get("text"))
            .contains(stubSettings.getMessage())
            .contains("15м");
    }

    private <T> AbstractAssert<?, ?> assertRecursively(T actual) {
        return assertThat(actual).usingRecursiveComparison();
    }
}
