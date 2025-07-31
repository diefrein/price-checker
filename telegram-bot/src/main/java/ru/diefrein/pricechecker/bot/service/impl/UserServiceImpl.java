package ru.diefrein.pricechecker.bot.service.impl;

import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.exception.DuplicateEntityException;
import ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.common.client.CheckerServiceClient;
import ru.diefrein.pricechecker.common.client.dto.CreateCheckerUserRequest;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final CheckerServiceClient checkerServiceClient;

    public UserServiceImpl(UserRepository repository, CheckerServiceClient checkerServiceClient) {
        this.repository = repository;
        this.checkerServiceClient = checkerServiceClient;
    }

    @Override
    public void create(long chatId, String name) {
        try {
            findByTelegramId(chatId);
            throw new DuplicateEntityException("User with chatId=%s already exists".formatted(chatId));
        } catch (EntityNotFoundException e) {
            UUID checkerUserId = checkerServiceClient.createUser(new CreateCheckerUserRequest(name, true));
            repository.create(chatId, checkerUserId);
        }
    }

    @Override
    public User findByTelegramId(long chatId) {
        return repository.findByTelegramId(chatId);
    }

    @Override
    public User findByCheckerUserId(UUID userId) {
        return repository.findByCheckerUserId(userId);
    }

    @Override
    public void updateStateByTelegramId(long chatId, UserState state) {
        repository.updateStateByTelegramId(chatId, state);
    }
}
