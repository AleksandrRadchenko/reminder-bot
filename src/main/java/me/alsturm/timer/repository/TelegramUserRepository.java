package me.alsturm.timer.repository;

import me.alsturm.timer.entity.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {

    Iterable<TelegramUser> findAllByIsActive(boolean isActive);
}
