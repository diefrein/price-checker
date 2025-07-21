package ru.diefrein.pricechecker.storage.repository;

import ru.diefrein.pricechecker.storage.dto.Page;
import ru.diefrein.pricechecker.storage.dto.PageRequest;
import ru.diefrein.pricechecker.storage.entity.User;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User create(String name, boolean isActive);

    Page<User> findActiveUsers(Connection conn, PageRequest pageRequest);

    User findById(UUID id);

    List<User> findAll();

    void remove(UUID id);

    void update(UUID id, boolean isActive);
}
