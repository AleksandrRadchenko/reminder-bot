package me.alsturm.reminder.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import me.alsturm.reminder.IntegrationTestBase;
import me.alsturm.reminder.mocks.MockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = "timer.accumulating-duration=40ms")
class CheckerServiceIT extends IntegrationTestBase {
    @MockBean
    UpdateProcessorImpl updateProcessor;
    @Autowired
    CheckerService checkerService;
    @Autowired
    GroupingUpdatesAccumulator updatesAccumulator;

    @Mock Update update1;
    @Mock Update update2;
    @Mock Update update3;
    @Mock Update update4;
    @Mock Update update5;
    @Mock Message message1;
    @Mock Message message2;
    @Mock Message message3;
    private final Long stubUserId = 1L;
    User stubUser = new MockUser(stubUserId);
    private final Long stubUser2Id = 2L;
    User stubUser2 = new MockUser(stubUser2Id);

    @BeforeEach
    void init() {
        when(update1.message()).thenReturn(message1);
        when(message1.from()).thenReturn(stubUser);
        when(update2.message()).thenReturn(message2);
        when(message2.from()).thenReturn(stubUser);
        when(update3.message()).thenReturn(message3);
        when(message3.from()).thenReturn(stubUser2);
        when(update4.message()).thenReturn(null);
    }

    @Test
    void whenUpdate_Processed() throws InterruptedException {
        checkerService.init();
        //act
        updatesAccumulator.accumulateUpdates(List.of(update1));
        Thread.sleep(10);
        updatesAccumulator.accumulateUpdates(List.of(update2));
        Thread.sleep(10);
        updatesAccumulator.accumulateUpdates(List.of(update3));
        Thread.sleep(10);
        updatesAccumulator.accumulateUpdates(List.of(update4));
        Thread.sleep(30);
        updatesAccumulator.accumulateUpdates(List.of(update5));
        //
        verify(updateProcessor).process(List.of(update1, update2));
        verify(updateProcessor).process(List.of(update3));
        verify(updateProcessor).process(List.of(update4));
        verify(updateProcessor).process(List.of(update5));
    }
}
