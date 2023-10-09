package me.alsturm.timer.repository;

import me.alsturm.timer.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    @SuppressWarnings("SpringDataMethodInconsistencyInspection") //Idea false-positive
    Iterable<TelegramUser> findAllByIsActive(boolean isActive);
}
