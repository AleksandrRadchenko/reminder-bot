package me.alsturm.reminder.repository;

import me.alsturm.reminder.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    @SuppressWarnings("SpringDataMethodInconsistencyInspection") //Idea false-positive
    Iterable<TelegramUser> findAllByIsActive(boolean isActive);
}
