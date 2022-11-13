package me.alsturm.timer;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTestBase {
    @MockBean
    protected TelegramBot telegramBot; // disable Bot
    @MockBean
    protected TaskScheduler taskScheduler;

    @BeforeEach
    void init() {
        doNothing().when(telegramBot).setUpdatesListener(any());
    }
}
