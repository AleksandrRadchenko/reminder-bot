package me.alsturm.reminder;

import com.pengrad.telegrambot.TelegramBot;
import me.alsturm.reminder.service.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class IntegrationTestBase {
    @MockBean
    protected TelegramBot telegramBot; // disable Bot
    @MockBean
    protected TaskScheduler taskScheduler;
    @Autowired
    TimeProvider timeProvider;

    @BeforeEach
    void init() {
        doNothing().when(telegramBot).setUpdatesListener(any());
    }
}
