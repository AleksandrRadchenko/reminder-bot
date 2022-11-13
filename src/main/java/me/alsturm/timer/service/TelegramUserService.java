package me.alsturm.timer.service;

import lombok.extern.slf4j.Slf4j;
import me.alsturm.timer.entity.TelegramUser;
import me.alsturm.timer.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@SuppressWarnings({"unused"})
@Slf4j
@Service
public class TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;


    public TelegramUserService(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    public TelegramUser save(TelegramUser telegramUser) throws NoSuchMethodException {
        throw new NoSuchMethodException("Use `activateUser` instead to keep `join_date` value.");
    }

    public Optional<TelegramUser> findById(Long id) {
        return telegramUserRepository.findById(id);
    }

    public Iterable<TelegramUser> findAllByIsActive(boolean active) {
        return telegramUserRepository.findAllByIsActive(active);
    }

    @Transactional
    public TelegramUser activateUser(TelegramUser user) {
        log.info("Creating user or marking user as active...");
        Optional<TelegramUser> mayBeExistingTelegramUser = telegramUserRepository.findById(user.getId());
        TelegramUser existingTelegramUser;
        if (mayBeExistingTelegramUser.isPresent()) {
            log.info("User exists. Marking active...");
            existingTelegramUser = mayBeExistingTelegramUser.get();
            return existingTelegramUser.setActive(true);
        } else {
            log.info("New user. Saving...");
            return telegramUserRepository.save(user);
        }
    }

    @Transactional
    public void deactivateUser(TelegramUser user) {
        Optional<TelegramUser> maybeTelegramUser = findById(user.getId());
        if (maybeTelegramUser.isPresent()) {
            TelegramUser telegramUser = maybeTelegramUser.get();
            telegramUser.setActive(false);
        } else {
            log.warn("User not found to mark inactive");
        }
    }
}
