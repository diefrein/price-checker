package ru.diefrein.pricechecker.service.impl;

import ru.diefrein.pricechecker.service.UserService;
import ru.diefrein.pricechecker.storage.entity.User;
import ru.diefrein.pricechecker.storage.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User create(String name, boolean isActive) {
        return repository.create(name, isActive);
    }

    @Override
    public User findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void update(UUID id, boolean isActive) {
        repository.update(id, isActive);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

}
