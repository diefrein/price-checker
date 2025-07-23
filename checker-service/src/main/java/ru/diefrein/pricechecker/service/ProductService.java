package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.storage.entity.Product;

import java.util.List;
import java.util.UUID;

/**
 * Service to work with products
 */
public interface ProductService {

    /**
     * Create new product
     *
     * @param userId user who is following product's updates
     * @param link link to product's page
     */
    void create(UUID userId, String link);

    /**
     * Check for product's info updates and notify users
     */
    void checkForUpdates();

    /**
     * Get list of products by user who follows them
     *
     * @param userId id of user
     * @return list of products
     */
    List<Product> findByUserId(UUID userId);
}
