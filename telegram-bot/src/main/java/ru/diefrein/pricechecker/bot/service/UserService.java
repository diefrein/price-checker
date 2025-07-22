package ru.diefrein.pricechecker.bot.service;


import ru.diefrein.pricechecker.bot.storage.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(long telegramId, String name);

    User findByTelegramId(long telegramId);

    User findByCheckerUserId(UUID userId);

    List<User> findAll();
}
