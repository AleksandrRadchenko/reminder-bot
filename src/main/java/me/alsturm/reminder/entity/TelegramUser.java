package me.alsturm.reminder.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.Instant;

/**
 * Would rather use com.pengrad.telegrambot.model.User but it
 * has no constructors or setters.
 */
@Entity
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TelegramUser {
    @Id
    private Long id;
    private Boolean isBot;
    private String firstName;
    private String lastName;
    private String username;
    private String languageCode;
    private Instant joinDate;
    private boolean isActive;
    @SuppressWarnings("JpaDataSourceORMInspection") //Idea false-positive
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private UserSettings userSettings;

    public String toShortString() {
        return firstName + " " + lastName + " (" + username + ")";
    }
}