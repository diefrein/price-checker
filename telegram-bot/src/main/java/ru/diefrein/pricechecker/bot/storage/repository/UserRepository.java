package ru.diefrein.pricechecker.bot.storage.repository;

import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.storage.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User create(long telegramId, UUID checkerUserId);

    User findByTelegramId(long telegramId);

    List<User> findAll();

    User findByCheckerUserId(UUID userId);

    void updateStateByTelegramId(long telegramId, UserState state);
}
