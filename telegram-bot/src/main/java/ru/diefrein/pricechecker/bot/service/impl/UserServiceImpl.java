package ru.diefrein.pricechecker.bot.service.impl;

import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.bot.transport.http.client.CheckerServiceClient;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CreateCheckerUserRequest;

import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final CheckerServiceClient checkerServiceClient;

    public UserServiceImpl(UserRepository repository, CheckerServiceClient checkerServiceClient) {
        this.repository = repository;
        this.checkerServiceClient = checkerServiceClient;
    }

    @Override
    public User create(long telegramId, String name) {
        UUID checkerUserId = checkerServiceClient.createUser(new CreateCheckerUserRequest(name, true));
        return repository.create(telegramId, checkerUserId);
    }

    @Override
    public User findByTelegramId(long telegramId) {
        return repository.findByTelegramId(telegramId);
    }

    @Override
    public User findByCheckerUserId(UUID userId) {
        return repository.findByCheckerUserId(userId);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }
}
