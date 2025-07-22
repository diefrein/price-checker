package ru.diefrein.pricechecker.bot.service;


import ru.diefrein.pricechecker.bot.storage.entity.User;

import java.util.List;

public interface UserService {

    User create(long telegramId, String name);

    User findByTelegramId(long telegramId);

    List<User> findAll();
}
