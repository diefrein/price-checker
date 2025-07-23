package ru.diefrein.pricechecker.bot.service;


import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.storage.entity.User;

import java.util.UUID;

public interface UserService {

    User create(long telegramId, String name);

    User findByTelegramId(long telegramId);

    User findByCheckerUserId(UUID userId);

    void updateStateByTelegramId(long telegramId, UserState state);
}
