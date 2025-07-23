package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.storage.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * Service to work with users
 */
public interface UserService {

    /**
     * Create new user
     *
     * @param name     user name
     * @param isActive flag whether user will receive updates or not
     * @return persisted object
     */
    User create(String name, boolean isActive);

    /**
     * Get user by id
     *
     * @param id id of user
     * @return use object
     */
    User findById(UUID id);

    /**
     * Update user
     *
     * @param id       id of user
     * @param isActive flag whether user will receive updates or not
     */
    void update(UUID id, boolean isActive);

    /**
     * Get list of all users
     *
     * @return users
     */
    List<User> findAll();
}
