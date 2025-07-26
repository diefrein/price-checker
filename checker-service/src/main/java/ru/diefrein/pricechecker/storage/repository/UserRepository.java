package ru.diefrein.pricechecker.storage.repository;

import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;
import ru.diefrein.pricechecker.storage.entity.User;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/**
 * User persistence layer
 */
public interface UserRepository {

    /**
     * Create new user
     *
     * @param name     user name
     * @param isActive flag whether user will receive updates or not
     * @return persisted object
     */
    User create(String name, boolean isActive);

    /**
     * Get page of active users
     *
     * @param conn        database connection
     * @param pageRequest pagination request
     * @return page of users
     */
    Page<User> findActiveUsers(Connection conn, PageRequest pageRequest);

    /**
     * Get user by id
     *
     * @param id id of user
     * @return use object
     */
    User findById(UUID id);

    /**
     * Get list of all users
     *
     * @return users
     */
    List<User> findAll();

    /**
     * Delete user by id
     *
     * @param id id of user
     */
    void remove(UUID id);

    /**
     * Update user
     *
     * @param id       id of user
     * @param isActive flag whether user will receive updates or not
     */
    void update(UUID id, boolean isActive);
}
