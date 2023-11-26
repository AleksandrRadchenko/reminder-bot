package me.alsturm.reminder.mocks;

import com.pengrad.telegrambot.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MockUser extends User {
    private Long id;

    private Boolean isBot;
    private String firstName;
    private String lastName;
    private String username;
    private String languageCode;
    private Boolean isPremium;
    private Boolean addedToAttachmentMenu;
    private Boolean canJoinGroups;
    private Boolean canReadAllGroupMessages;
    private Boolean supportsInlineQueries;

    public MockUser(Long id) {
        super(id);
    }

    @Override
    public Boolean isBot() {
        return isBot;
    }

    @Override
    public Boolean isPremium() {
        return isPremium;
    }
}
