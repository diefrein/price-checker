package ru.diefrein.pricechecker.storage.repository;

import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;
import ru.diefrein.pricechecker.storage.entity.Product;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/**
 * Product persistence layer
 */
public interface ProductRepository {

    /**
     * Create new product. Also inserts entry to product history
     *
     * @param userId      user who is following product's updates
     * @param link        link to product's page
     * @param name        product name
     * @param actualPrice price at moment of save
     * @return product object
     */
    Product create(UUID userId, String link, String name, Double actualPrice);

    /**
     * Updates product. Also inserts entry to product history
     *
     * @param conn        database connection
     * @param productId   id of product
     * @param name        product name
     * @param actualPrice price at moment of save
     */
    void update(Connection conn, UUID productId, String name, Double actualPrice);

    /**
     * Get products page followed by specified user
     *
     * @param conn        database connection
     * @param userId      id of user
     * @param pageRequest pagination request
     * @return page of products
     */
    Page<Product> findByUserId(Connection conn, UUID userId, PageRequest pageRequest);

    /**
     * Get products page followed by specified user
     *
     * @param userId      id of user
     * @param pageRequest pagination request
     * @return page of products
     */
    Page<Product> findByUserId(UUID userId, PageRequest pageRequest);

    /**
     * Get product by id
     *
     * @param id id of product
     * @return product object
     */
    Product findById(UUID id);

    /**
     * Remove product. Also removes all associated history
     *
     * @param id id of product
     */
    void remove(UUID id);
}
