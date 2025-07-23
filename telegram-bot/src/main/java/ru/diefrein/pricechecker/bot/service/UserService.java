package ru.diefrein.pricechecker.bot.service;


import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.storage.entity.User;

import java.util.UUID;

/**
 * Service to work with bot users
 */
public interface UserService {

    /**
     * Create new user. Also creates entity in checker-db and stores its id
     *
     * @param telegramId id of chat with user
     * @param name       user name
     */
    void create(long telegramId, String name);

    /**
     * Get user by chat id
     *
     * @param telegramId id of chat with user
     * @return user
     * @throws ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException if no user found
     */
    User findByTelegramId(long telegramId);

    /**
     * Get user by checker_user_id
     *
     * @param userId id of user in checker-db
     * @return user
     * @throws ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException if no user found
     */
    User findByCheckerUserId(UUID userId);

    /**
     * Updates state of user by chat id
     *
     * @param telegramId id of chat with user
     * @param state new user's state
     */
    void updateStateByTelegramId(long telegramId, UserState state);
}
