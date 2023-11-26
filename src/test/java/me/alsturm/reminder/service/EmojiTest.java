package me.alsturm.reminder.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Specify BOT_TOKEN in env. Look for existing configuration in `services`.
 */
class EmojiTest {
    private final String bot_token = System.getProperty("BOT_TOKEN");
    private final TelegramBot bot = new TelegramBot(bot_token);

    @Test
    @Disabled("For manual execution")
    void emojiChecker() {
        if (bot_token == null) {
            fail("Please specify BOT_TOKEN env variable.");
        }
        int userId = 85941983;
        String ticket = "\uD83C\uDFAB"; //🎫
        String calendarEmoji = "\uD83D\uDCC5"; //📅
        String heartEmoji = "\u2764"; //❤
        String stethoscopeEmoji = "\uD83E\uDE7A"; //🩺

        String response = "`" + stethoscopeEmoji + "Айболит` "
            + calendarEmoji + "Талонов: *" + 5001 + "*"
            + System.lineSeparator();
        final SendMessage sendMessageRequest = new SendMessage(userId, response).parseMode(ParseMode.Markdown);
        final SendResponse sendResponse = bot.execute(sendMessageRequest);

        assertThat(sendResponse).isNotNull();
    }
}
