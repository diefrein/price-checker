package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.storage.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(String name, boolean isActive);

    User findById(UUID id);

    void update(UUID id, boolean isActive);

    List<User> findAll();
}
