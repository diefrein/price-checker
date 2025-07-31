package ru.diefrein.pricechecker.storage.repository;

import ru.diefrein.pricechecker.storage.entity.ProductHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Product history persistence layer
 */
public interface ProductHistoryRepository {

    /**
     * Create history entry
     *
     * @param productId       id of followed product
     * @param price           new price
     * @param priceAtDateTime moment of saving
     */
    void create(UUID productId, Double price, LocalDateTime priceAtDateTime);

    /**
     * Get full history of product
     *
     * @param productId id of followed product
     * @return list of history entries
     */
    List<ProductHistory> findByProductId(UUID productId);
}
