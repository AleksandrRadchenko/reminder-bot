package me.alsturm.reminder;

import com.pengrad.telegrambot.TelegramBot;
import me.alsturm.reminder.mapper.TelegramUserConverter;
import me.alsturm.reminder.repository.TelegramUserRepository;
import me.alsturm.reminder.repository.UserSettingsRepository;
import me.alsturm.reminder.service.CheckerService;
import me.alsturm.reminder.service.GroupingUpdatesAccumulator;
import me.alsturm.reminder.service.TimeProvider;
import me.alsturm.reminder.service.UpdateProcessorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class IntegrationTestBase {
    @MockBean
    protected TelegramBot telegramBot; // disable Bot

    @Autowired
    protected TaskScheduler taskScheduler;
    @Autowired
    protected TimeProvider timeProvider;
    @Autowired
    protected Clock clock;
    @Autowired
    protected CheckerService checkerService;
    @Autowired
    protected GroupingUpdatesAccumulator updatesAccumulator;
    @Autowired
    protected UpdateProcessorImpl updateProcessor;
    @Autowired
    protected TelegramUserRepository telegramUserRepository;
    @Autowired
    protected UserSettingsRepository userSettingsRepository;
    @Autowired
    protected TelegramUserConverter telegramUserConverter;

    @BeforeEach
    void init() {
        doNothing().when(telegramBot).setUpdatesListener(any());
    }
}
