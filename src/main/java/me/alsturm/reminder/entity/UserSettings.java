package me.alsturm.reminder.entity;

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Type;

import java.time.Duration;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UserSettings {
    @Id
    private Long telegramUserId;
    private String message;
    @Type(PostgreSQLIntervalType.class)
    @Column(columnDefinition = "interval")
    private Duration delay;
}
