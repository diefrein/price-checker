package ru.diefrein.pricechecker.bot.storage.repository;

import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.storage.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * User's persistence layer
 */
public interface UserRepository {

    /**
     * Create user
     *
     * @param chatId id of chat with user
     * @param checkerUserId if of user in checker-db
     */
    void create(long chatId, UUID checkerUserId);

    /**
     * Get user by chat id
     *
     * @param chatId id of chat with user
     * @return user
     * @throws ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException if no user found
     */
    User findByTelegramId(long chatId);

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
     * @param chatId id of chat with user
     * @param state new user's state
     */
    void updateStateByTelegramId(long chatId, UserState state);
}
