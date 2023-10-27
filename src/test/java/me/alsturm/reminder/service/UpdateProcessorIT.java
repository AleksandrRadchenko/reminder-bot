package me.alsturm.reminder.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import me.alsturm.reminder.IntegrationTestBase;
import me.alsturm.reminder.entity.UserSettings;
import me.alsturm.reminder.mapper.TelegramUserConverter;
import me.alsturm.reminder.entity.TelegramUser;
import me.alsturm.reminder.mocks.MockUser;
import me.alsturm.reminder.model.ReminderCommand;
import me.alsturm.reminder.repository.TelegramUserRepository;
import me.alsturm.reminder.repository.UserSettingsRepository;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;

import static me.alsturm.reminder.model.ReminderCommand.COMMAND_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
class UpdateProcessorIT extends IntegrationTestBase {
    @Autowired
    UpdateProcessorImpl updateProcessor;
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
        when(message.text()).thenReturn(COMMAND_PREFIX + ReminderCommand.START.aliases.get(0));

        updateProcessor.process(List.of(update));

        TelegramUser actualUser = telegramUserRepository.findById(stubUserId).get();
        TelegramUser expectedUser = telegramUserConverter.fromUser(stubUser).setActive(true);
        assertRecursively(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void whenUserIssuesStopCommand_MarkedAsInactive() {
        when(message.text()).thenReturn(COMMAND_PREFIX + ReminderCommand.STOP.aliases.get(0));
        telegramUserRepository.save(telegramUserConverter.fromUser(stubUser));

        updateProcessor.process(List.of(update));

        TelegramUser actualUser = telegramUserRepository.findById(stubUserId).get();
        TelegramUser expectedUser = telegramUserConverter.fromUser(stubUser).setActive(false);
        assertRecursively(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void whenUserIssuesSetCommand_UserSettingsDisplayed() {
        when(message.text()).thenReturn(COMMAND_PREFIX + ReminderCommand.SET.aliases.get(0));
        userSettingsRepository.save(stubSettings);

        updateProcessor.process(List.of(update));

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
